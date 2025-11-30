package me.sathish.aws_refresh.service;

import java.util.List;
import me.sathish.aws_refresh.domain.BatchJobRun;
import me.sathish.aws_refresh.domain.EmailTemplate;
import me.sathish.aws_refresh.domain.JobRunEmail;
import me.sathish.aws_refresh.events.BeforeDeleteBatchJobRun;
import me.sathish.aws_refresh.events.BeforeDeleteEmailTemplate;
import me.sathish.aws_refresh.model.JobRunEmailDTO;
import me.sathish.aws_refresh.repos.BatchJobRunRepository;
import me.sathish.aws_refresh.repos.EmailTemplateRepository;
import me.sathish.aws_refresh.repos.JobRunEmailRepository;
import me.sathish.aws_refresh.util.NotFoundException;
import me.sathish.aws_refresh.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class JobRunEmailServiceImpl implements JobRunEmailService {

    private final JobRunEmailRepository jobRunEmailRepository;
    private final BatchJobRunRepository batchJobRunRepository;
    private final EmailTemplateRepository emailTemplateRepository;

    public JobRunEmailServiceImpl(final JobRunEmailRepository jobRunEmailRepository,
            final BatchJobRunRepository batchJobRunRepository,
            final EmailTemplateRepository emailTemplateRepository) {
        this.jobRunEmailRepository = jobRunEmailRepository;
        this.batchJobRunRepository = batchJobRunRepository;
        this.emailTemplateRepository = emailTemplateRepository;
    }

    @Override
    public List<JobRunEmailDTO> findAll() {
        final List<JobRunEmail> jobRunEmails = jobRunEmailRepository.findAll(Sort.by("id"));
        return jobRunEmails.stream()
                .map(jobRunEmail -> mapToDTO(jobRunEmail, new JobRunEmailDTO()))
                .toList();
    }

    @Override
    public JobRunEmailDTO get(final Long id) {
        return jobRunEmailRepository.findById(id)
                .map(jobRunEmail -> mapToDTO(jobRunEmail, new JobRunEmailDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final JobRunEmailDTO jobRunEmailDTO) {
        final JobRunEmail jobRunEmail = new JobRunEmail();
        mapToEntity(jobRunEmailDTO, jobRunEmail);
        return jobRunEmailRepository.save(jobRunEmail).getId();
    }

    @Override
    public void update(final Long id, final JobRunEmailDTO jobRunEmailDTO) {
        final JobRunEmail jobRunEmail = jobRunEmailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(jobRunEmailDTO, jobRunEmail);
        jobRunEmailRepository.save(jobRunEmail);
    }

    @Override
    public void delete(final Long id) {
        final JobRunEmail jobRunEmail = jobRunEmailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        jobRunEmailRepository.delete(jobRunEmail);
    }

    private JobRunEmailDTO mapToDTO(final JobRunEmail jobRunEmail,
            final JobRunEmailDTO jobRunEmailDTO) {
        jobRunEmailDTO.setId(jobRunEmail.getId());
        jobRunEmailDTO.setRecipientEmail(jobRunEmail.getRecipientEmail());
        jobRunEmailDTO.setRecipientName(jobRunEmail.getRecipientName());
        jobRunEmailDTO.setSubject(jobRunEmail.getSubject());
        jobRunEmailDTO.setBody(jobRunEmail.getBody());
        jobRunEmailDTO.setStatus(jobRunEmail.getStatus());
        jobRunEmailDTO.setSentAt(jobRunEmail.getSentAt());
        jobRunEmailDTO.setErrorMessage(jobRunEmail.getErrorMessage());
        jobRunEmailDTO.setCreatedAt(jobRunEmail.getCreatedAt());
        jobRunEmailDTO.setUpdatedAt(jobRunEmail.getUpdatedAt());
        jobRunEmailDTO.setJobRun(jobRunEmail.getJobRun() == null ? null : jobRunEmail.getJobRun().getId());
        jobRunEmailDTO.setTemplate(jobRunEmail.getTemplate() == null ? null : jobRunEmail.getTemplate().getId());
        return jobRunEmailDTO;
    }

    private JobRunEmail mapToEntity(final JobRunEmailDTO jobRunEmailDTO,
            final JobRunEmail jobRunEmail) {
        jobRunEmail.setRecipientEmail(jobRunEmailDTO.getRecipientEmail());
        jobRunEmail.setRecipientName(jobRunEmailDTO.getRecipientName());
        jobRunEmail.setSubject(jobRunEmailDTO.getSubject());
        jobRunEmail.setBody(jobRunEmailDTO.getBody());
        jobRunEmail.setStatus(jobRunEmailDTO.getStatus());
        jobRunEmail.setSentAt(jobRunEmailDTO.getSentAt());
        jobRunEmail.setErrorMessage(jobRunEmailDTO.getErrorMessage());
        jobRunEmail.setCreatedAt(jobRunEmailDTO.getCreatedAt());
        jobRunEmail.setUpdatedAt(jobRunEmailDTO.getUpdatedAt());
        final BatchJobRun jobRun = jobRunEmailDTO.getJobRun() == null ? null : batchJobRunRepository.findById(jobRunEmailDTO.getJobRun())
                .orElseThrow(() -> new NotFoundException("jobRun not found"));
        jobRunEmail.setJobRun(jobRun);
        final EmailTemplate template = jobRunEmailDTO.getTemplate() == null ? null : emailTemplateRepository.findById(jobRunEmailDTO.getTemplate())
                .orElseThrow(() -> new NotFoundException("template not found"));
        jobRunEmail.setTemplate(template);
        return jobRunEmail;
    }

    @EventListener(BeforeDeleteBatchJobRun.class)
    public void on(final BeforeDeleteBatchJobRun event) {
        final ReferencedException referencedException = new ReferencedException();
        final JobRunEmail jobRunJobRunEmail = jobRunEmailRepository.findFirstByJobRunId(event.getId());
        if (jobRunJobRunEmail != null) {
            referencedException.setKey("batchJobRun.jobRunEmail.jobRun.referenced");
            referencedException.addParam(jobRunJobRunEmail.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteEmailTemplate.class)
    public void on(final BeforeDeleteEmailTemplate event) {
        final ReferencedException referencedException = new ReferencedException();
        final JobRunEmail templateJobRunEmail = jobRunEmailRepository.findFirstByTemplateId(event.getId());
        if (templateJobRunEmail != null) {
            referencedException.setKey("emailTemplate.jobRunEmail.template.referenced");
            referencedException.addParam(templateJobRunEmail.getId());
            throw referencedException;
        }
    }

}
