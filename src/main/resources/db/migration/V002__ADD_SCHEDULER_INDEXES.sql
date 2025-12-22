-- Add indexes for better query performance on scheduled jobs

CREATE INDEX idx_batch_job_active_cron ON batch_job(is_active, schedule_cron) WHERE is_active = true AND schedule_cron IS NOT NULL;

CREATE INDEX idx_batch_job_run_job_id ON batch_job_run(job_id);

CREATE INDEX idx_batch_job_run_status ON batch_job_run(status);

CREATE INDEX idx_batch_job_run_started_at ON batch_job_run(started_at DESC);

CREATE INDEX idx_batch_job_run_job_status ON batch_job_run(job_id, status, started_at DESC);

COMMENT ON INDEX idx_batch_job_active_cron IS 'Index for finding active jobs with cron schedules';
COMMENT ON INDEX idx_batch_job_run_job_id IS 'Index for finding runs by job ID';
COMMENT ON INDEX idx_batch_job_run_status IS 'Index for filtering runs by status';
COMMENT ON INDEX idx_batch_job_run_started_at IS 'Index for sorting runs by start time';
COMMENT ON INDEX idx_batch_job_run_job_status IS 'Composite index for job run queries';
