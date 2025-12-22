package me.sathish.aws_refresh.service;

import lombok.extern.slf4j.Slf4j;
import me.sathish.aws_refresh.config.BatchJobProperties;
import me.sathish.aws_refresh.domain.BatchJob;
import me.sathish.aws_refresh.domain.BatchJobRun;
import me.sathish.aws_refresh.repos.BatchJobRepository;
import me.sathish.aws_refresh.repos.BatchJobRunRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BatchJobSchedulerService {

    private final BatchJobRepository batchJobRepository;
    private final BatchJobRunRepository batchJobRunRepository;
    private final TaskScheduler taskScheduler;
    private final BatchJobProperties properties;
    private final Map<String, BatchJobExecutor> executorRegistry;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks;
    private final Map<Long, String> currentCronExpressions;

    public BatchJobSchedulerService(
            BatchJobRepository batchJobRepository,
            BatchJobRunRepository batchJobRunRepository,
            TaskScheduler taskScheduler,
            BatchJobProperties properties,
            List<BatchJobExecutor> executors) {
        this.batchJobRepository = batchJobRepository;
        this.batchJobRunRepository = batchJobRunRepository;
        this.taskScheduler = taskScheduler;
        this.properties = properties;
        this.executorRegistry = new ConcurrentHashMap<>();
        this.scheduledTasks = new ConcurrentHashMap<>();
        this.currentCronExpressions = new ConcurrentHashMap<>();
        
        executors.forEach(executor -> {
            log.info("Registering batch job executor: {}", executor.getJobName());
            executorRegistry.put(executor.getJobName(), executor);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeScheduler() {
        if (!properties.isEnableScheduling()) {
            log.warn("Batch job scheduling is disabled");
            return;
        }
        
        log.info("Initializing batch job scheduler...");
        refreshScheduledJobs();
        log.info("Batch job scheduler initialized successfully");
    }

    @Scheduled(fixedDelayString = "${batch.job.scheduler-refresh-interval-minutes:5}", timeUnit = TimeUnit.MINUTES)
    public void refreshScheduledJobs() {
        if (!properties.isEnableScheduling()) {
            return;
        }
        
        log.debug("Refreshing scheduled jobs...");
        
        try {
            List<BatchJob> activeJobs = batchJobRepository.findByIsActiveTrueAndScheduleCronIsNotNull();
            
            for (BatchJob job : activeJobs) {
                scheduleOrUpdateJob(job);
            }
            
            removeInactiveJobs(activeJobs);
            
            log.info("Scheduled jobs refreshed. Active jobs: {}", scheduledTasks.size());
        } catch (Exception e) {
            log.error("Error refreshing scheduled jobs", e);
        }
    }

    private void scheduleOrUpdateJob(BatchJob job) {
        try {
            String cronExpression = job.getScheduleCron();
            
            if (cronExpression == null || cronExpression.trim().isEmpty()) {
                log.debug("Job {} has no cron expression, skipping", job.getId());
                cancelScheduledJob(job.getId());
                return;
            }
            
            if (!isValidCronExpression(cronExpression)) {
                log.error("Invalid cron expression for job {}: {}", job.getId(), cronExpression);
                return;
            }
            
            String currentCron = currentCronExpressions.get(job.getId());
            
            if (cronExpression.equals(currentCron)) {
                log.debug("Cron expression unchanged for job {}, skipping reschedule", job.getId());
                return;
            }
            
            cancelScheduledJob(job.getId());
            
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> executeJobAsync(job.getId()),
                new CronTrigger(cronExpression)
            );
            
            scheduledTasks.put(job.getId(), scheduledTask);
            currentCronExpressions.put(job.getId(), cronExpression);
            
            log.info("Scheduled job: {} (ID: {}) with cron: {}", job.getName(), job.getId(), cronExpression);
            
        } catch (Exception e) {
            log.error("Error scheduling job: {} (ID: {})", job.getName(), job.getId(), e);
        }
    }

    private void removeInactiveJobs(List<BatchJob> activeJobs) {
        List<Long> activeJobIds = activeJobs.stream()
            .map(BatchJob::getId)
            .toList();
        
        scheduledTasks.keySet().stream()
            .filter(jobId -> !activeJobIds.contains(jobId))
            .forEach(this::cancelScheduledJob);
    }

    private void cancelScheduledJob(Long jobId) {
        ScheduledFuture<?> task = scheduledTasks.remove(jobId);
        if (task != null) {
            task.cancel(false);
            currentCronExpressions.remove(jobId);
            log.info("Cancelled scheduled job: {}", jobId);
        }
    }

    private boolean isValidCronExpression(String cronExpression) {
        try {
            CronExpression.parse(cronExpression);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void executeJobAsync(Long jobId) {
        try {
            BatchJob job = batchJobRepository.findById(jobId).orElse(null);
            
            if (job == null) {
                log.warn("Job {} not found, cannot execute", jobId);
                return;
            }
            
            if (!job.getIsActive()) {
                log.info("Job {} is inactive, skipping execution", jobId);
                return;
            }
            
            log.info("Triggering scheduled execution for job: {} (ID: {})", job.getName(), jobId);
            executeJob(jobId, "SCHEDULED");
            
        } catch (Exception e) {
            log.error("Error executing scheduled job: {}", jobId, e);
        }
    }

    @Transactional
    public BatchJobRun executeJob(Long jobId, String triggerType) {
        BatchJob job = batchJobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
        
        log.info("Executing job: {} (ID: {}) with trigger type: {}", job.getName(), jobId, triggerType);
        
        BatchJobRun jobRun = new BatchJobRun();
        jobRun.setJob(job);
        jobRun.setTriggerType(triggerType);
        jobRun.setStartedAt(OffsetDateTime.now());
        jobRun.setStatus("RUNNING");
        jobRun.setCreatedAt(OffsetDateTime.now());
        jobRun.setUpdatedAt(OffsetDateTime.now());
        
        jobRun = batchJobRunRepository.save(jobRun);
        
        try {
            BatchJobExecutor executor = findExecutorForJob(job);
            
            if (executor == null) {
                log.warn("No executor found for job: {} (ID: {}), using default execution", job.getName(), jobId);
                executeDefaultJob(jobRun);
            } else {
                BatchJobRun executedRun = executor.executeJob(job, triggerType);
                jobRun.setStatus(executedRun.getStatus());
                jobRun.setFinishedAt(executedRun.getFinishedAt());
                jobRun.setResultSummary(executedRun.getResultSummary());
                jobRun.setErrorMessage(executedRun.getErrorMessage());
                jobRun.setInputParams(executedRun.getInputParams());
            }
            
            jobRun.setUpdatedAt(OffsetDateTime.now());
            jobRun = batchJobRunRepository.save(jobRun);
            
            log.info("Job execution completed: {} (ID: {}) with status: {}", 
                job.getName(), jobId, jobRun.getStatus());
            
        } catch (Exception e) {
            log.error("Error executing job: {} (ID: {})", job.getName(), jobId, e);
            jobRun.setStatus("FAILED");
            jobRun.setErrorMessage(e.getMessage());
            jobRun.setFinishedAt(OffsetDateTime.now());
            jobRun.setUpdatedAt(OffsetDateTime.now());
            jobRun = batchJobRunRepository.save(jobRun);
        }
        
        return jobRun;
    }

    private BatchJobExecutor findExecutorForJob(BatchJob job) {
        return executorRegistry.values().stream()
            .filter(executor -> executor.getJobName().equalsIgnoreCase(job.getName()))
            .findFirst()
            .orElse(null);
    }

    private void executeDefaultJob(BatchJobRun jobRun) {
        log.info("Executing default job logic for: {}", jobRun.getJob().getName());
        
        try {
            Thread.sleep(1000);
            jobRun.setStatus("COMPLETED");
            jobRun.setResultSummary("Default job execution completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            jobRun.setStatus("FAILED");
            jobRun.setErrorMessage("Job interrupted: " + e.getMessage());
        } finally {
            jobRun.setFinishedAt(OffsetDateTime.now());
        }
    }

    public void rescheduleJob(Long jobId) {
        BatchJob job = batchJobRepository.findById(jobId).orElse(null);
        if (job != null) {
            scheduleOrUpdateJob(job);
        }
    }

    public void cancelJob(Long jobId) {
        cancelScheduledJob(jobId);
    }

    public Map<Long, String> getScheduledJobsStatus() {
        Map<Long, String> status = new ConcurrentHashMap<>();
        scheduledTasks.forEach((jobId, task) -> {
            String cronExpression = currentCronExpressions.get(jobId);
            status.put(jobId, String.format("Scheduled with cron: %s, Cancelled: %s", 
                cronExpression, task.isCancelled()));
        });
        return status;
    }
}
