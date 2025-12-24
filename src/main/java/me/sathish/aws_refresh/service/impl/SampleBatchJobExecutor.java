package me.sathish.aws_refresh.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.sathish.aws_refresh.domain.BatchJob;
import me.sathish.aws_refresh.domain.BatchJobRun;
import me.sathish.aws_refresh.service.BatchJobExecutor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.ListRolesResponse;
import software.amazon.awssdk.services.iam.model.Role;

import java.time.OffsetDateTime;
import java.util.List;

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
            // List all IAM roles
            try (IamClient iamClient = IamClient.builder().region(Region.AWS_GLOBAL).build()) {
                ListRolesResponse response = iamClient.listRoles();
                List<Role> roles = response.roles();

                log.info("Found {} IAM roles:", roles.size());
                for (Role role : roles) {
                    log.info("Role: {} (ARN: {}, Created: {})",
                            role.roleName(),
                            role.arn(),
                            role.createDate());
                }

                jobRun.setResultSummary("Found " + roles.size() + " IAM roles");
            }

            jobRun.setStatus("COMPLETED");
            log.info("Sample batch job completed: {} (ID: {})", job.getName(), job.getId());

        } catch (Exception e) {
            log.error("Error executing sample batch job: {} (ID: {})", job.getName(), job.getId(), e);
            jobRun.setStatus("FAILED");
            jobRun.setErrorMessage(e.getMessage());
        } finally {
            jobRun.setFinishedAt(OffsetDateTime.now());
            jobRun.setUpdatedAt(OffsetDateTime.now());
        }
//            Thread.sleep(200000);
            

        
        return jobRun;
    }

    @Override
    public String getJobName() {
        return "SAMPLE_JOB";
    }
}
