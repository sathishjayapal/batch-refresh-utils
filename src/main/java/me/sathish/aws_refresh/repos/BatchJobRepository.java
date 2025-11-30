package me.sathish.aws_refresh.repos;

import me.sathish.aws_refresh.domain.BatchJob;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BatchJobRepository extends JpaRepository<BatchJob, Long> {
}
