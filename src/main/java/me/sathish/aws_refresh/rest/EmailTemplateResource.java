package me.sathish.aws_refresh.rest;

import jakarta.validation.Valid;
import java.util.List;
import me.sathish.aws_refresh.model.EmailTemplateDTO;
import me.sathish.aws_refresh.service.EmailTemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/emailTemplates", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmailTemplateResource {

    private final EmailTemplateService emailTemplateService;

    public EmailTemplateResource(final EmailTemplateService emailTemplateService) {
        this.emailTemplateService = emailTemplateService;
    }

    @GetMapping
    public ResponseEntity<List<EmailTemplateDTO>> getAllEmailTemplates() {
        return ResponseEntity.ok(emailTemplateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailTemplateDTO> getEmailTemplate(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(emailTemplateService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createEmailTemplate(
            @RequestBody @Valid final EmailTemplateDTO emailTemplateDTO) {
        final Long createdId = emailTemplateService.create(emailTemplateDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateEmailTemplate(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final EmailTemplateDTO emailTemplateDTO) {
        emailTemplateService.update(id, emailTemplateDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmailTemplate(@PathVariable(name = "id") final Long id) {
        emailTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
