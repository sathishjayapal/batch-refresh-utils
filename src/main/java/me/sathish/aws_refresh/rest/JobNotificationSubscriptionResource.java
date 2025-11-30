package me.sathish.aws_refresh.rest;

import jakarta.validation.Valid;
import java.util.List;
import me.sathish.aws_refresh.model.JobNotificationSubscriptionDTO;
import me.sathish.aws_refresh.service.JobNotificationSubscriptionService;
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
@RequestMapping(value = "/api/jobNotificationSubscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
public class JobNotificationSubscriptionResource {

    private final JobNotificationSubscriptionService jobNotificationSubscriptionService;

    public JobNotificationSubscriptionResource(
            final JobNotificationSubscriptionService jobNotificationSubscriptionService) {
        this.jobNotificationSubscriptionService = jobNotificationSubscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<JobNotificationSubscriptionDTO>> getAllJobNotificationSubscriptions(
            ) {
        return ResponseEntity.ok(jobNotificationSubscriptionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobNotificationSubscriptionDTO> getJobNotificationSubscription(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(jobNotificationSubscriptionService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createJobNotificationSubscription(
            @RequestBody @Valid final JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO) {
        final Long createdId = jobNotificationSubscriptionService.create(jobNotificationSubscriptionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateJobNotificationSubscription(
            @PathVariable(name = "id") final Long id,
            @RequestBody @Valid final JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO) {
        jobNotificationSubscriptionService.update(id, jobNotificationSubscriptionDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobNotificationSubscription(
            @PathVariable(name = "id") final Long id) {
        jobNotificationSubscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
