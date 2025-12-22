package me.sathish.aws_refresh.rest;

import lombok.extern.slf4j.Slf4j;
import me.sathish.aws_refresh.domain.BatchJobRun;
import me.sathish.aws_refresh.service.BatchJobSchedulerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/batch-scheduler", produces = MediaType.APPLICATION_JSON_VALUE)
public class BatchJobSchedulerResource {

    private final BatchJobSchedulerService schedulerService;

    public BatchJobSchedulerResource(BatchJobSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @PostMapping("/jobs/{jobId}/execute")
    public ResponseEntity<Map<String, Object>> executeJob(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "MANUAL") String triggerType) {
        
        log.info("Manual execution requested for job: {} with trigger type: {}", jobId, triggerType);
        
        try {
            BatchJobRun jobRun = schedulerService.executeJob(jobId, triggerType);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Job execution initiated",
                "jobRunId", jobRun.getId(),
                "status", jobRun.getStatus()
            ));
        } catch (Exception e) {
            log.error("Error executing job: {}", jobId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to execute job: " + e.getMessage()
                ));
        }
    }

    @PostMapping("/jobs/{jobId}/reschedule")
    public ResponseEntity<Map<String, Object>> rescheduleJob(@PathVariable Long jobId) {
        log.info("Reschedule requested for job: {}", jobId);
        
        try {
            schedulerService.rescheduleJob(jobId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Job rescheduled successfully"
            ));
        } catch (Exception e) {
            log.error("Error rescheduling job: {}", jobId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to reschedule job: " + e.getMessage()
                ));
        }
    }

    @PostMapping("/jobs/{jobId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelJob(@PathVariable Long jobId) {
        log.info("Cancel requested for job: {}", jobId);
        
        try {
            schedulerService.cancelJob(jobId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Job cancelled successfully"
            ));
        } catch (Exception e) {
            log.error("Error cancelling job: {}", jobId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to cancel job: " + e.getMessage()
                ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSchedulerStatus() {
        try {
            Map<Long, String> scheduledJobs = schedulerService.getScheduledJobsStatus();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "scheduledJobsCount", scheduledJobs.size(),
                "scheduledJobs", scheduledJobs
            ));
        } catch (Exception e) {
            log.error("Error getting scheduler status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to get scheduler status: " + e.getMessage()
                ));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshScheduler() {
        log.info("Manual scheduler refresh requested");
        
        try {
            schedulerService.refreshScheduledJobs();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Scheduler refreshed successfully"
            ));
        } catch (Exception e) {
            log.error("Error refreshing scheduler", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to refresh scheduler: " + e.getMessage()
                ));
        }
    }
}
