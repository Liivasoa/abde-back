package mg.msys.abde_back.language.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mg.msys.abde_back.application.usecase.language.AddLanguageUseCase;
import mg.msys.abde_back.domain.model.Language;
import mg.msys.abde_back.language.in.web.dto.LanguageDto;
import mg.msys.abde_back.shared.infrastructure.adapter.in.web.dto.ResourceResponse;
import mg.msys.abde_back.shared.infrastructure.adapter.in.web.dto.ErrorResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/language")
@RequiredArgsConstructor
@Tag(name = "Language", description = "Manage language resources")
public class LanguageController {

        private final AddLanguageUseCase addLanguageUseCase;

        @PostMapping
        @Operation(summary = "Create a new language", description = "Creates a new language with the provided code and label")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Language created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input - business rule violated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<ResourceResponse> addLanguage(
                        @RequestBody LanguageDto request) {

                Language createdLanguage = addLanguageUseCase.execute(request.getCode(), request.getLabel());

                ResourceResponse response = ResourceResponse.builder()
                                .id(createdLanguage.getCode())
                                .location("/api/language/" + createdLanguage.getCode())
                                .message("Language created successfully")
                                .build();

                return ResponseEntity
                                .created(URI.create("/api/language/" + createdLanguage.getCode()))
                                .body(response);
        }
}
