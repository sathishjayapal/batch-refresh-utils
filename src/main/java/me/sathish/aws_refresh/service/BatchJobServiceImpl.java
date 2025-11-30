package me.sathish.aws_refresh.service;

import java.util.List;
import java.util.Map;
import me.sathish.aws_refresh.domain.BatchJob;
import me.sathish.aws_refresh.events.BeforeDeleteBatchJob;
import me.sathish.aws_refresh.model.BatchJobDTO;
import me.sathish.aws_refresh.repos.BatchJobRepository;
import me.sathish.aws_refresh.util.CustomCollectors;
import me.sathish.aws_refresh.util.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class BatchJobServiceImpl implements BatchJobService {

    private final BatchJobRepository batchJobRepository;
    private final ApplicationEventPublisher publisher;

    public BatchJobServiceImpl(final BatchJobRepository batchJobRepository,
            final ApplicationEventPublisher publisher) {
        this.batchJobRepository = batchJobRepository;
        this.publisher = publisher;
    }

    @Override
    public List<BatchJobDTO> findAll() {
        final List<BatchJob> batchJobs = batchJobRepository.findAll(Sort.by("id"));
        return batchJobs.stream()
                .map(batchJob -> mapToDTO(batchJob, new BatchJobDTO()))
                .toList();
    }

    @Override
    public BatchJobDTO get(final Long id) {
        return batchJobRepository.findById(id)
                .map(batchJob -> mapToDTO(batchJob, new BatchJobDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final BatchJobDTO batchJobDTO) {
        final BatchJob batchJob = new BatchJob();
        mapToEntity(batchJobDTO, batchJob);
        return batchJobRepository.save(batchJob).getId();
    }

    @Override
    public void update(final Long id, final BatchJobDTO batchJobDTO) {
        final BatchJob batchJob = batchJobRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(batchJobDTO, batchJob);
        batchJobRepository.save(batchJob);
    }

    @Override
    public void delete(final Long id) {
        final BatchJob batchJob = batchJobRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteBatchJob(id));
        batchJobRepository.delete(batchJob);
    }

    private BatchJobDTO mapToDTO(final BatchJob batchJob, final BatchJobDTO batchJobDTO) {
        batchJobDTO.setId(batchJob.getId());
        batchJobDTO.setName(batchJob.getName());
        batchJobDTO.setDescription(batchJob.getDescription());
        batchJobDTO.setScheduleCron(batchJob.getScheduleCron());
        batchJobDTO.setIsActive(batchJob.getIsActive());
        batchJobDTO.setCreatedAt(batchJob.getCreatedAt());
        batchJobDTO.setUpdatedAt(batchJob.getUpdatedAt());
        return batchJobDTO;
    }

    private BatchJob mapToEntity(final BatchJobDTO batchJobDTO, final BatchJob batchJob) {
        batchJob.setName(batchJobDTO.getName());
        batchJob.setDescription(batchJobDTO.getDescription());
        batchJob.setScheduleCron(batchJobDTO.getScheduleCron());
        batchJob.setIsActive(batchJobDTO.getIsActive());
        batchJob.setCreatedAt(batchJobDTO.getCreatedAt());
        batchJob.setUpdatedAt(batchJobDTO.getUpdatedAt());
        return batchJob;
    }

    @Override
    public Map<Long, String> getBatchJobValues() {
        return batchJobRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(BatchJob::getId, BatchJob::getName));
    }

}
