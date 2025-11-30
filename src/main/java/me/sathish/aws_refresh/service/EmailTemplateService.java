package me.sathish.aws_refresh.service;

import java.util.List;
import java.util.Map;
import me.sathish.aws_refresh.model.EmailTemplateDTO;


public interface EmailTemplateService {

    List<EmailTemplateDTO> findAll();

    EmailTemplateDTO get(Long id);

    Long create(EmailTemplateDTO emailTemplateDTO);

    void update(Long id, EmailTemplateDTO emailTemplateDTO);

    void delete(Long id);

    Map<Long, String> getEmailTemplateValues();

}
