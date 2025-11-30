package me.sathish.aws_refresh.repos;

import me.sathish.aws_refresh.domain.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
}
