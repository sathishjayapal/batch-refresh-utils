package me.sathish.aws_refresh.service;

import java.util.List;
import java.util.Map;
import me.sathish.aws_refresh.model.BatchJobDTO;


public interface BatchJobService {

    List<BatchJobDTO> findAll();

    BatchJobDTO get(Long id);

    Long create(BatchJobDTO batchJobDTO);

    void update(Long id, BatchJobDTO batchJobDTO);

    void delete(Long id);

    Map<Long, String> getBatchJobValues();

}
