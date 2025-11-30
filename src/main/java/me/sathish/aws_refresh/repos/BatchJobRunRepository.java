package me.sathish.aws_refresh.repos;

import me.sathish.aws_refresh.domain.BatchJobRun;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BatchJobRunRepository extends JpaRepository<BatchJobRun, Long> {

    BatchJobRun findFirstByJobId(Long id);

}
