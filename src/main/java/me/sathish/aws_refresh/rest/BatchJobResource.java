package me.sathish.aws_refresh.rest;

import jakarta.validation.Valid;
import java.util.List;
import me.sathish.aws_refresh.model.BatchJobDTO;
import me.sathish.aws_refresh.service.BatchJobService;
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
@RequestMapping(value = "/api/batchJobs", produces = MediaType.APPLICATION_JSON_VALUE)
public class BatchJobResource {

    private final BatchJobService batchJobService;

    public BatchJobResource(final BatchJobService batchJobService) {
        this.batchJobService = batchJobService;
    }

    @GetMapping
    public ResponseEntity<List<BatchJobDTO>> getAllBatchJobs() {
        return ResponseEntity.ok(batchJobService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchJobDTO> getBatchJob(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(batchJobService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createBatchJob(@RequestBody @Valid final BatchJobDTO batchJobDTO) {
        final Long createdId = batchJobService.create(batchJobDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateBatchJob(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final BatchJobDTO batchJobDTO) {
        batchJobService.update(id, batchJobDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatchJob(@PathVariable(name = "id") final Long id) {
        batchJobService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
