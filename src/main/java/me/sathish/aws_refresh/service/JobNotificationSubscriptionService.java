package me.sathish.aws_refresh.service;

import java.util.List;
import me.sathish.aws_refresh.model.JobNotificationSubscriptionDTO;


public interface JobNotificationSubscriptionService {

    List<JobNotificationSubscriptionDTO> findAll();

    JobNotificationSubscriptionDTO get(Long id);

    Long create(JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO);

    void update(Long id, JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO);

    void delete(Long id);

}
