package me.sathish.aws_refresh.service;

import java.util.List;
import me.sathish.aws_refresh.model.JobRunEmailDTO;


public interface JobRunEmailService {

    List<JobRunEmailDTO> findAll();

    JobRunEmailDTO get(Long id);

    Long create(JobRunEmailDTO jobRunEmailDTO);

    void update(Long id, JobRunEmailDTO jobRunEmailDTO);

    void delete(Long id);

}
