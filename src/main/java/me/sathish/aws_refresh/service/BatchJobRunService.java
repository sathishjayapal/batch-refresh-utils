package me.sathish.aws_refresh.service;

import java.util.List;
import java.util.Map;
import me.sathish.aws_refresh.model.BatchJobRunDTO;


public interface BatchJobRunService {

    List<BatchJobRunDTO> findAll();

    BatchJobRunDTO get(Long id);

    Long create(BatchJobRunDTO batchJobRunDTO);

    void update(Long id, BatchJobRunDTO batchJobRunDTO);

    void delete(Long id);

    Map<Long, String> getBatchJobRunValues();

}
