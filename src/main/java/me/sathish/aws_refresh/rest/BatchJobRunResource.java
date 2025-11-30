package me.sathish.aws_refresh.rest;

import jakarta.validation.Valid;
import java.util.List;
import me.sathish.aws_refresh.model.BatchJobRunDTO;
import me.sathish.aws_refresh.service.BatchJobRunService;
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
@RequestMapping(value = "/api/batchJobRuns", produces = MediaType.APPLICATION_JSON_VALUE)
public class BatchJobRunResource {

    private final BatchJobRunService batchJobRunService;

    public BatchJobRunResource(final BatchJobRunService batchJobRunService) {
        this.batchJobRunService = batchJobRunService;
    }

    @GetMapping
    public ResponseEntity<List<BatchJobRunDTO>> getAllBatchJobRuns() {
        return ResponseEntity.ok(batchJobRunService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchJobRunDTO> getBatchJobRun(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(batchJobRunService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createBatchJobRun(
            @RequestBody @Valid final BatchJobRunDTO batchJobRunDTO) {
        final Long createdId = batchJobRunService.create(batchJobRunDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateBatchJobRun(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final BatchJobRunDTO batchJobRunDTO) {
        batchJobRunService.update(id, batchJobRunDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatchJobRun(@PathVariable(name = "id") final Long id) {
        batchJobRunService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
