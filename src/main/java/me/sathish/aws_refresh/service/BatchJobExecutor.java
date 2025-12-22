package me.sathish.aws_refresh.service;

import me.sathish.aws_refresh.domain.BatchJob;
import me.sathish.aws_refresh.domain.BatchJobRun;

public interface BatchJobExecutor {

    BatchJobRun executeJob(BatchJob job, String triggerType);

    String getJobName();
}
