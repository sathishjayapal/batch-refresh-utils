package me.sathish.aws_refresh.rest;

import jakarta.validation.Valid;
import java.util.List;
import me.sathish.aws_refresh.model.JobRunEmailDTO;
import me.sathish.aws_refresh.service.JobRunEmailService;
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
@RequestMapping(value = "/api/jobRunEmails", produces = MediaType.APPLICATION_JSON_VALUE)
public class JobRunEmailResource {

    private final JobRunEmailService jobRunEmailService;

    public JobRunEmailResource(final JobRunEmailService jobRunEmailService) {
        this.jobRunEmailService = jobRunEmailService;
    }

    @GetMapping
    public ResponseEntity<List<JobRunEmailDTO>> getAllJobRunEmails() {
        return ResponseEntity.ok(jobRunEmailService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobRunEmailDTO> getJobRunEmail(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(jobRunEmailService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createJobRunEmail(
            @RequestBody @Valid final JobRunEmailDTO jobRunEmailDTO) {
        final Long createdId = jobRunEmailService.create(jobRunEmailDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateJobRunEmail(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final JobRunEmailDTO jobRunEmailDTO) {
        jobRunEmailService.update(id, jobRunEmailDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobRunEmail(@PathVariable(name = "id") final Long id) {
        jobRunEmailService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
