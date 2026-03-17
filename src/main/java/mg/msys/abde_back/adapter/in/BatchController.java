package mg.msys.abde_back.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.adapter.in.dto.ErrorResponse;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Tag(name = "Batch", description = "Trigger batch jobs")
public class BatchController {

    private final JobOperator jobOperator;
    private final Job importBooksJob;

    @PostMapping("/import-books")
    @Operation(summary = "Import books", description = "Triggers the books import batch from pg_catalog.csv")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job started successfully"),
            @ApiResponse(responseCode = "500", description = "Job failed to start", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, String>> importBooks() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();
            jobOperator.start(importBooksJob, params);
            return ResponseEntity.ok(Map.of("message", "Job importBooksJob started successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Job failed to start: " + e.getMessage()));
        }
    }
}
