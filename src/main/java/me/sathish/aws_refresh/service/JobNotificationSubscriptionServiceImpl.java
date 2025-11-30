package me.sathish.aws_refresh.service;

import java.util.List;
import me.sathish.aws_refresh.domain.BatchJob;
import me.sathish.aws_refresh.domain.EmailTemplate;
import me.sathish.aws_refresh.domain.JobNotificationSubscription;
import me.sathish.aws_refresh.events.BeforeDeleteBatchJob;
import me.sathish.aws_refresh.events.BeforeDeleteEmailTemplate;
import me.sathish.aws_refresh.model.JobNotificationSubscriptionDTO;
import me.sathish.aws_refresh.repos.BatchJobRepository;
import me.sathish.aws_refresh.repos.EmailTemplateRepository;
import me.sathish.aws_refresh.repos.JobNotificationSubscriptionRepository;
import me.sathish.aws_refresh.util.NotFoundException;
import me.sathish.aws_refresh.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class JobNotificationSubscriptionServiceImpl implements JobNotificationSubscriptionService {

    private final JobNotificationSubscriptionRepository jobNotificationSubscriptionRepository;
    private final BatchJobRepository batchJobRepository;
    private final EmailTemplateRepository emailTemplateRepository;

    public JobNotificationSubscriptionServiceImpl(
            final JobNotificationSubscriptionRepository jobNotificationSubscriptionRepository,
            final BatchJobRepository batchJobRepository,
            final EmailTemplateRepository emailTemplateRepository) {
        this.jobNotificationSubscriptionRepository = jobNotificationSubscriptionRepository;
        this.batchJobRepository = batchJobRepository;
        this.emailTemplateRepository = emailTemplateRepository;
    }

    @Override
    public List<JobNotificationSubscriptionDTO> findAll() {
        final List<JobNotificationSubscription> jobNotificationSubscriptions = jobNotificationSubscriptionRepository.findAll(Sort.by("id"));
        return jobNotificationSubscriptions.stream()
                .map(jobNotificationSubscription -> mapToDTO(jobNotificationSubscription, new JobNotificationSubscriptionDTO()))
                .toList();
    }

    @Override
    public JobNotificationSubscriptionDTO get(final Long id) {
        return jobNotificationSubscriptionRepository.findById(id)
                .map(jobNotificationSubscription -> mapToDTO(jobNotificationSubscription, new JobNotificationSubscriptionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO) {
        final JobNotificationSubscription jobNotificationSubscription = new JobNotificationSubscription();
        mapToEntity(jobNotificationSubscriptionDTO, jobNotificationSubscription);
        return jobNotificationSubscriptionRepository.save(jobNotificationSubscription).getId();
    }

    @Override
    public void update(final Long id,
            final JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO) {
        final JobNotificationSubscription jobNotificationSubscription = jobNotificationSubscriptionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(jobNotificationSubscriptionDTO, jobNotificationSubscription);
        jobNotificationSubscriptionRepository.save(jobNotificationSubscription);
    }

    @Override
    public void delete(final Long id) {
        final JobNotificationSubscription jobNotificationSubscription = jobNotificationSubscriptionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        jobNotificationSubscriptionRepository.delete(jobNotificationSubscription);
    }

    private JobNotificationSubscriptionDTO mapToDTO(
            final JobNotificationSubscription jobNotificationSubscription,
            final JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO) {
        jobNotificationSubscriptionDTO.setId(jobNotificationSubscription.getId());
        jobNotificationSubscriptionDTO.setRecipientEmail(jobNotificationSubscription.getRecipientEmail());
        jobNotificationSubscriptionDTO.setNotifyOn(jobNotificationSubscription.getNotifyOn());
        jobNotificationSubscriptionDTO.setCreatedAt(jobNotificationSubscription.getCreatedAt());
        jobNotificationSubscriptionDTO.setUpdatedAt(jobNotificationSubscription.getUpdatedAt());
        jobNotificationSubscriptionDTO.setJob(jobNotificationSubscription.getJob() == null ? null : jobNotificationSubscription.getJob().getId());
        jobNotificationSubscriptionDTO.setTemplate(jobNotificationSubscription.getTemplate() == null ? null : jobNotificationSubscription.getTemplate().getId());
        return jobNotificationSubscriptionDTO;
    }

    private JobNotificationSubscription mapToEntity(
            final JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO,
            final JobNotificationSubscription jobNotificationSubscription) {
        jobNotificationSubscription.setRecipientEmail(jobNotificationSubscriptionDTO.getRecipientEmail());
        jobNotificationSubscription.setNotifyOn(jobNotificationSubscriptionDTO.getNotifyOn());
        jobNotificationSubscription.setCreatedAt(jobNotificationSubscriptionDTO.getCreatedAt());
        jobNotificationSubscription.setUpdatedAt(jobNotificationSubscriptionDTO.getUpdatedAt());
        final BatchJob job = jobNotificationSubscriptionDTO.getJob() == null ? null : batchJobRepository.findById(jobNotificationSubscriptionDTO.getJob())
                .orElseThrow(() -> new NotFoundException("job not found"));
        jobNotificationSubscription.setJob(job);
        final EmailTemplate template = jobNotificationSubscriptionDTO.getTemplate() == null ? null : emailTemplateRepository.findById(jobNotificationSubscriptionDTO.getTemplate())
                .orElseThrow(() -> new NotFoundException("template not found"));
        jobNotificationSubscription.setTemplate(template);
        return jobNotificationSubscription;
    }

    @EventListener(BeforeDeleteBatchJob.class)
    public void on(final BeforeDeleteBatchJob event) {
        final ReferencedException referencedException = new ReferencedException();
        final JobNotificationSubscription jobJobNotificationSubscription = jobNotificationSubscriptionRepository.findFirstByJobId(event.getId());
        if (jobJobNotificationSubscription != null) {
            referencedException.setKey("batchJob.jobNotificationSubscription.job.referenced");
            referencedException.addParam(jobJobNotificationSubscription.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteEmailTemplate.class)
    public void on(final BeforeDeleteEmailTemplate event) {
        final ReferencedException referencedException = new ReferencedException();
        final JobNotificationSubscription templateJobNotificationSubscription = jobNotificationSubscriptionRepository.findFirstByTemplateId(event.getId());
        if (templateJobNotificationSubscription != null) {
            referencedException.setKey("emailTemplate.jobNotificationSubscription.template.referenced");
            referencedException.addParam(templateJobNotificationSubscription.getId());
            throw referencedException;
        }
    }

}
