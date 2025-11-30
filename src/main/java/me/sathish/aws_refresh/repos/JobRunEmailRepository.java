package me.sathish.aws_refresh.repos;

import me.sathish.aws_refresh.domain.JobRunEmail;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JobRunEmailRepository extends JpaRepository<JobRunEmail, Long> {

    JobRunEmail findFirstByJobRunId(Long id);

    JobRunEmail findFirstByTemplateId(Long id);

}
