# Batch Job Scheduler Documentation

## Overview

The Batch Job Scheduler is a production-ready, cron-based job scheduling system that dynamically schedules and executes batch jobs based on their cron expressions stored in the database.

## Features

### Core Features
- **Dynamic Cron Scheduling**: Jobs are scheduled based on cron expressions stored in the `schedule_cron` field
- **Automatic Refresh**: Scheduler automatically refreshes every 5 minutes to pick up new/updated jobs
- **Manual Job Execution**: Trigger jobs manually via REST API
- **Job Lifecycle Management**: Create, update, delete, and reschedule jobs dynamically
- **Execution Tracking**: All job executions are tracked in `batch_job_run` table with status, timestamps, and results

### Production-Ready Features
- **Health Checks**: Actuator health endpoint shows scheduler status
- **Comprehensive Logging**: SLF4J logging at all critical points
- **Error Handling**: Graceful error handling with detailed error messages
- **Thread Pool Management**: Configurable thread pools for scheduling and execution
- **Graceful Shutdown**: Waits for running tasks to complete on shutdown
- **Metrics Support**: Exposed via Spring Boot Actuator

## Architecture

### Components

1. **BatchJobSchedulerService**: Core scheduler service that manages job scheduling
2. **BatchJobExecutor**: Interface for implementing custom job execution logic
3. **SampleBatchJobExecutor**: Sample implementation of job executor
4. **BatchJobSchedulerResource**: REST API for manual job control
5. **BatchJobHealthIndicator**: Health check indicator for monitoring
6. **SchedulerConfig**: Spring configuration for task scheduler
7. **BatchJobProperties**: Configuration properties for scheduler settings

## Configuration

### Application Properties

```yaml
batch:
  job:
    scheduler-pool-size: 10                    # Thread pool size for scheduler
    executor-pool-size: 5                      # Thread pool size for job execution
    executor-queue-capacity: 100               # Queue capacity for pending jobs
    max-retry-attempts: 3                      # Max retry attempts for failed jobs
    retry-delay-millis: 5000                   # Delay between retries
    job-timeout-minutes: 60                    # Job execution timeout
    enable-scheduling: true                    # Enable/disable scheduling
    scheduler-refresh-interval-minutes: 5      # Interval to refresh scheduled jobs
```

## Usage

### 1. Creating a Scheduled Job

**Via REST API:**
```bash
POST /api/batchJobs
Content-Type: application/json

{
  "name": "Daily Data Refresh",
  "description": "Refreshes data from AWS S3 daily",
  "scheduleCron": "0 0 2 * * ?",  # Every day at 2 AM
  "isActive": true,
  "createdAt": "2024-12-21T10:00:00Z",
  "updatedAt": "2024-12-21T10:00:00Z"
}
```

**Via Web UI:**
Navigate to `/batchJobs/add` and fill in the form.

### 2. Cron Expression Examples

```
0 0 2 * * ?        # Every day at 2 AM
0 */15 * * * ?     # Every 15 minutes
0 0 */6 * * ?      # Every 6 hours
0 0 0 * * MON      # Every Monday at midnight
0 0 12 1 * ?       # First day of every month at noon
```

### 3. Manual Job Execution

```bash
POST /api/batch-scheduler/jobs/{jobId}/execute?triggerType=MANUAL
```

### 4. Reschedule a Job

```bash
POST /api/batch-scheduler/jobs/{jobId}/reschedule
```

### 5. Cancel a Scheduled Job

```bash
POST /api/batch-scheduler/jobs/{jobId}/cancel
```

### 6. Check Scheduler Status

```bash
GET /api/batch-scheduler/status
```

Response:
```json
{
  "success": true,
  "scheduledJobsCount": 3,
  "scheduledJobs": {
    "10001": "Scheduled with cron: 0 0 2 * * ?, Cancelled: false",
    "10002": "Scheduled with cron: 0 */15 * * * ?, Cancelled: false"
  }
}
```

### 7. Refresh Scheduler

```bash
POST /api/batch-scheduler/refresh
```

## Implementing Custom Job Executors

To implement custom job execution logic:

1. Create a class that implements `BatchJobExecutor`:

```java
@Slf4j
@Component
public class S3DataRefreshExecutor implements BatchJobExecutor {

    @Override
    public BatchJobRun executeJob(BatchJob job, String triggerType) {
        log.info("Executing S3 data refresh job: {}", job.getName());
        
        BatchJobRun jobRun = new BatchJobRun();
        jobRun.setJob(job);
        jobRun.setTriggerType(triggerType);
        jobRun.setStartedAt(OffsetDateTime.now());
        jobRun.setStatus("RUNNING");
        jobRun.setCreatedAt(OffsetDateTime.now());
        jobRun.setUpdatedAt(OffsetDateTime.now());
        
        try {
            // Your custom job logic here
            refreshDataFromS3();
            
            jobRun.setStatus("COMPLETED");
            jobRun.setResultSummary("Successfully refreshed data from S3");
            
        } catch (Exception e) {
            log.error("Error executing S3 refresh job", e);
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
        return "S3_DATA_REFRESH";  // Must match the job name in database
    }
    
    private void refreshDataFromS3() {
        // Implementation
    }
}
```

2. The executor will be automatically registered and used when a job with matching name is executed.

## Monitoring

### Health Check

```bash
GET /actuator/health
```

Response includes scheduler status:
```json
{
  "status": "UP",
  "components": {
    "batchJobHealthIndicator": {
      "status": "UP",
      "details": {
        "scheduledJobsCount": 3,
        "schedulingEnabled": true,
        "scheduledJobs": {...}
      }
    }
  }
}
```

### Metrics

```bash
GET /actuator/metrics
```

## Job Execution Flow

1. **Scheduler Initialization**: On application startup, scheduler loads all active jobs with cron expressions
2. **Job Scheduling**: Each job is scheduled using Spring's `TaskScheduler` with its cron expression
3. **Automatic Refresh**: Every 5 minutes, scheduler checks for new/updated jobs
4. **Job Execution**: When cron triggers, job is executed asynchronously
5. **Execution Tracking**: Job run is created with status, timestamps, and results
6. **Error Handling**: Errors are caught, logged, and stored in job run

## Database Schema

### batch_job Table
- `id`: Job ID
- `name`: Job name (used to match with executor)
- `description`: Job description
- `schedule_cron`: Cron expression for scheduling
- `is_active`: Whether job is active
- `created_at`, `updated_at`: Timestamps

### batch_job_run Table
- `id`: Run ID
- `job_id`: Reference to batch_job
- `started_at`, `finished_at`: Execution timestamps
- `status`: RUNNING, COMPLETED, FAILED
- `trigger_type`: SCHEDULED, MANUAL, etc.
- `input_params`: Input parameters (JSON)
- `result_summary`: Execution result summary
- `error_message`: Error details if failed
- `created_at`, `updated_at`: Timestamps

## Troubleshooting

### Job Not Scheduling
1. Check if `is_active` is true
2. Verify `schedule_cron` is valid
3. Check logs for scheduler errors
4. Verify `batch.job.enable-scheduling` is true

### Job Execution Fails
1. Check job run table for error messages
2. Review application logs
3. Verify executor implementation
4. Check database connectivity

### Scheduler Not Refreshing
1. Check `batch.job.scheduler-refresh-interval-minutes` setting
2. Verify scheduler is enabled
3. Check application logs for errors

## Best Practices

1. **Cron Expressions**: Use standard cron format, test expressions before deploying
2. **Job Names**: Use descriptive, unique names for jobs
3. **Executors**: Implement proper error handling in custom executors
4. **Monitoring**: Regularly check health endpoint and job run history
5. **Logging**: Enable DEBUG logging for troubleshooting: `logging.level.me.sathish.aws_refresh.service=DEBUG`
6. **Database**: Regularly clean up old job run records
7. **Thread Pools**: Adjust pool sizes based on job volume and execution time

## API Reference

### Batch Job CRUD
- `GET /api/batchJobs` - List all jobs
- `GET /api/batchJobs/{id}` - Get job details
- `POST /api/batchJobs` - Create new job
- `PUT /api/batchJobs/{id}` - Update job
- `DELETE /api/batchJobs/{id}` - Delete job

### Scheduler Control
- `POST /api/batch-scheduler/jobs/{jobId}/execute` - Execute job manually
- `POST /api/batch-scheduler/jobs/{jobId}/reschedule` - Reschedule job
- `POST /api/batch-scheduler/jobs/{jobId}/cancel` - Cancel scheduled job
- `GET /api/batch-scheduler/status` - Get scheduler status
- `POST /api/batch-scheduler/refresh` - Refresh scheduler

### Job Runs
- `GET /api/batchJobRuns` - List all job runs
- `GET /api/batchJobRuns/{id}` - Get job run details

## Security Considerations

1. **API Security**: Add authentication/authorization to scheduler endpoints
2. **Input Validation**: Validate cron expressions before saving
3. **Rate Limiting**: Implement rate limiting for manual job execution
4. **Audit Logging**: Log all scheduler operations
5. **Database Access**: Use read-only connections where appropriate
