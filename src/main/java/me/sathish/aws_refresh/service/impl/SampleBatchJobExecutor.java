package me.sathish.aws_refresh.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.sathish.aws_refresh.domain.BatchJob;
import me.sathish.aws_refresh.domain.BatchJobRun;
import me.sathish.aws_refresh.service.BatchJobExecutor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
public class SampleBatchJobExecutor implements BatchJobExecutor {

    @Override
    public BatchJobRun executeJob(BatchJob job, String triggerType) {
        log.info("Executing sample batch job: {} (ID: {})", job.getName(), job.getId());
        
        BatchJobRun jobRun = new BatchJobRun();
        jobRun.setJob(job);
        jobRun.setTriggerType(triggerType);
        jobRun.setStartedAt(OffsetDateTime.now());
        jobRun.setStatus("RUNNING");
        jobRun.setCreatedAt(OffsetDateTime.now());
        jobRun.setUpdatedAt(OffsetDateTime.now());
        
        try {
            log.info("Sample job execution logic for: {}", job.getName());
            Thread.sleep(200000);
            
            jobRun.setStatus("COMPLETED");
            jobRun.setResultSummary("Sample job completed successfully");
            log.info("Sample batch job completed: {} (ID: {})", job.getName(), job.getId());
            
        } catch (Exception e) {
            log.error("Error executing sample batch job: {} (ID: {})", job.getName(), job.getId(), e);
            jobRun.setStatus("FAILED");
            jobRun.setErrorMessage(e.getMessage());
        } finally {
            jobRun.setFinishedAt(OffsetDateTime.now());
            jobRun.setUpdatedAt(OffsetDateTime.now());
        }
        
        return jobRun;
    }

    @Override
    public String getJobName() {
        return "SAMPLE_JOB";
    }
}
