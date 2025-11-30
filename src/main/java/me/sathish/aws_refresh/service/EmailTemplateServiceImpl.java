package me.sathish.aws_refresh.service;

import java.util.List;
import java.util.Map;
import me.sathish.aws_refresh.domain.EmailTemplate;
import me.sathish.aws_refresh.events.BeforeDeleteEmailTemplate;
import me.sathish.aws_refresh.model.EmailTemplateDTO;
import me.sathish.aws_refresh.repos.EmailTemplateRepository;
import me.sathish.aws_refresh.util.CustomCollectors;
import me.sathish.aws_refresh.util.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;
    private final ApplicationEventPublisher publisher;

    public EmailTemplateServiceImpl(final EmailTemplateRepository emailTemplateRepository,
            final ApplicationEventPublisher publisher) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.publisher = publisher;
    }

    @Override
    public List<EmailTemplateDTO> findAll() {
        final List<EmailTemplate> emailTemplates = emailTemplateRepository.findAll(Sort.by("id"));
        return emailTemplates.stream()
                .map(emailTemplate -> mapToDTO(emailTemplate, new EmailTemplateDTO()))
                .toList();
    }

    @Override
    public EmailTemplateDTO get(final Long id) {
        return emailTemplateRepository.findById(id)
                .map(emailTemplate -> mapToDTO(emailTemplate, new EmailTemplateDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final EmailTemplateDTO emailTemplateDTO) {
        final EmailTemplate emailTemplate = new EmailTemplate();
        mapToEntity(emailTemplateDTO, emailTemplate);
        return emailTemplateRepository.save(emailTemplate).getId();
    }

    @Override
    public void update(final Long id, final EmailTemplateDTO emailTemplateDTO) {
        final EmailTemplate emailTemplate = emailTemplateRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(emailTemplateDTO, emailTemplate);
        emailTemplateRepository.save(emailTemplate);
    }

    @Override
    public void delete(final Long id) {
        final EmailTemplate emailTemplate = emailTemplateRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteEmailTemplate(id));
        emailTemplateRepository.delete(emailTemplate);
    }

    private EmailTemplateDTO mapToDTO(final EmailTemplate emailTemplate,
            final EmailTemplateDTO emailTemplateDTO) {
        emailTemplateDTO.setId(emailTemplate.getId());
        emailTemplateDTO.setCode(emailTemplate.getCode());
        emailTemplateDTO.setSubjectTemplate(emailTemplate.getSubjectTemplate());
        emailTemplateDTO.setBodyTemplate(emailTemplate.getBodyTemplate());
        emailTemplateDTO.setIsActive(emailTemplate.getIsActive());
        emailTemplateDTO.setCreatedAt(emailTemplate.getCreatedAt());
        emailTemplateDTO.setUpdatedAt(emailTemplate.getUpdatedAt());
        return emailTemplateDTO;
    }

    private EmailTemplate mapToEntity(final EmailTemplateDTO emailTemplateDTO,
            final EmailTemplate emailTemplate) {
        emailTemplate.setCode(emailTemplateDTO.getCode());
        emailTemplate.setSubjectTemplate(emailTemplateDTO.getSubjectTemplate());
        emailTemplate.setBodyTemplate(emailTemplateDTO.getBodyTemplate());
        emailTemplate.setIsActive(emailTemplateDTO.getIsActive());
        emailTemplate.setCreatedAt(emailTemplateDTO.getCreatedAt());
        emailTemplate.setUpdatedAt(emailTemplateDTO.getUpdatedAt());
        return emailTemplate;
    }

    @Override
    public Map<Long, String> getEmailTemplateValues() {
        return emailTemplateRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(EmailTemplate::getId, EmailTemplate::getCode));
    }

}
