package me.sathish.aws_refresh.repos;

import me.sathish.aws_refresh.domain.JobNotificationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JobNotificationSubscriptionRepository extends JpaRepository<JobNotificationSubscription, Long> {

    JobNotificationSubscription findFirstByJobId(Long id);

    JobNotificationSubscription findFirstByTemplateId(Long id);

}
