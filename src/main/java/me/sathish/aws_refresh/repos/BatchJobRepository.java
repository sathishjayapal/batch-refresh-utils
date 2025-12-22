package me.sathish.aws_refresh.repos;

import me.sathish.aws_refresh.domain.BatchJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BatchJobRepository extends JpaRepository<BatchJob, Long> {

    List<BatchJob> findByIsActiveTrue();

    List<BatchJob> findByIsActiveTrueAndScheduleCronIsNotNull();
}
