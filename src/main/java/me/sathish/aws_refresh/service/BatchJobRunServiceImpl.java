package me.sathish.aws_refresh.service;

import java.util.List;
import java.util.Map;
import me.sathish.aws_refresh.domain.BatchJob;
import me.sathish.aws_refresh.domain.BatchJobRun;
import me.sathish.aws_refresh.events.BeforeDeleteBatchJob;
import me.sathish.aws_refresh.events.BeforeDeleteBatchJobRun;
import me.sathish.aws_refresh.model.BatchJobRunDTO;
import me.sathish.aws_refresh.repos.BatchJobRepository;
import me.sathish.aws_refresh.repos.BatchJobRunRepository;
import me.sathish.aws_refresh.util.CustomCollectors;
import me.sathish.aws_refresh.util.NotFoundException;
import me.sathish.aws_refresh.util.ReferencedException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class BatchJobRunServiceImpl implements BatchJobRunService {

    private final BatchJobRunRepository batchJobRunRepository;
    private final BatchJobRepository batchJobRepository;
    private final ApplicationEventPublisher publisher;

    public BatchJobRunServiceImpl(final BatchJobRunRepository batchJobRunRepository,
            final BatchJobRepository batchJobRepository,
            final ApplicationEventPublisher publisher) {
        this.batchJobRunRepository = batchJobRunRepository;
        this.batchJobRepository = batchJobRepository;
        this.publisher = publisher;
    }

    @Override
    public List<BatchJobRunDTO> findAll() {
        final List<BatchJobRun> batchJobRuns = batchJobRunRepository.findAll(Sort.by("id"));
        return batchJobRuns.stream()
                .map(batchJobRun -> mapToDTO(batchJobRun, new BatchJobRunDTO()))
                .toList();
    }

    @Override
    public BatchJobRunDTO get(final Long id) {
        return batchJobRunRepository.findById(id)
                .map(batchJobRun -> mapToDTO(batchJobRun, new BatchJobRunDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final BatchJobRunDTO batchJobRunDTO) {
        final BatchJobRun batchJobRun = new BatchJobRun();
        mapToEntity(batchJobRunDTO, batchJobRun);
        return batchJobRunRepository.save(batchJobRun).getId();
    }

    @Override
    public void update(final Long id, final BatchJobRunDTO batchJobRunDTO) {
        final BatchJobRun batchJobRun = batchJobRunRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(batchJobRunDTO, batchJobRun);
        batchJobRunRepository.save(batchJobRun);
    }

    @Override
    public void delete(final Long id) {
        final BatchJobRun batchJobRun = batchJobRunRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteBatchJobRun(id));
        batchJobRunRepository.delete(batchJobRun);
    }

    private BatchJobRunDTO mapToDTO(final BatchJobRun batchJobRun,
            final BatchJobRunDTO batchJobRunDTO) {
        batchJobRunDTO.setId(batchJobRun.getId());
        batchJobRunDTO.setStartedAt(batchJobRun.getStartedAt());
        batchJobRunDTO.setFinishedAt(batchJobRun.getFinishedAt());
        batchJobRunDTO.setStatus(batchJobRun.getStatus());
        batchJobRunDTO.setTriggerType(batchJobRun.getTriggerType());
        batchJobRunDTO.setInputParams(batchJobRun.getInputParams());
        batchJobRunDTO.setResultSummary(batchJobRun.getResultSummary());
        batchJobRunDTO.setErrorMessage(batchJobRun.getErrorMessage());
        batchJobRunDTO.setCreatedAt(batchJobRun.getCreatedAt());
        batchJobRunDTO.setUpdatedAt(batchJobRun.getUpdatedAt());
        batchJobRunDTO.setJob(batchJobRun.getJob() == null ? null : batchJobRun.getJob().getId());
        return batchJobRunDTO;
    }

    private BatchJobRun mapToEntity(final BatchJobRunDTO batchJobRunDTO,
            final BatchJobRun batchJobRun) {
        batchJobRun.setStartedAt(batchJobRunDTO.getStartedAt());
        batchJobRun.setFinishedAt(batchJobRunDTO.getFinishedAt());
        batchJobRun.setStatus(batchJobRunDTO.getStatus());
        batchJobRun.setTriggerType(batchJobRunDTO.getTriggerType());
        batchJobRun.setInputParams(batchJobRunDTO.getInputParams());
        batchJobRun.setResultSummary(batchJobRunDTO.getResultSummary());
        batchJobRun.setErrorMessage(batchJobRunDTO.getErrorMessage());
        batchJobRun.setCreatedAt(batchJobRunDTO.getCreatedAt());
        batchJobRun.setUpdatedAt(batchJobRunDTO.getUpdatedAt());
        final BatchJob job = batchJobRunDTO.getJob() == null ? null : batchJobRepository.findById(batchJobRunDTO.getJob())
                .orElseThrow(() -> new NotFoundException("job not found"));
        batchJobRun.setJob(job);
        return batchJobRun;
    }

    @Override
    public Map<Long, String> getBatchJobRunValues() {
        return batchJobRunRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(BatchJobRun::getId, BatchJobRun::getStatus));
    }

    @EventListener(BeforeDeleteBatchJob.class)
    public void on(final BeforeDeleteBatchJob event) {
        final ReferencedException referencedException = new ReferencedException();
        final BatchJobRun jobBatchJobRun = batchJobRunRepository.findFirstByJobId(event.getId());
        if (jobBatchJobRun != null) {
            referencedException.setKey("batchJob.batchJobRun.job.referenced");
            referencedException.addParam(jobBatchJobRun.getId());
            throw referencedException;
        }
    }

}
