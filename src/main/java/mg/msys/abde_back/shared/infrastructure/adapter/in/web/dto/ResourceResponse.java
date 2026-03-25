package mg.msys.abde_back.shared.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "Standard response for resource creation/modification")
public class ResourceResponse {

    @Schema(description = "Unique identifier of the created/modified resource")
    private String id;

    @Schema(description = "HTTP URI to access the created/modified resource", example = "/api/language/EN")
    private String location;

    @Schema(description = "Human-readable message about the operation", example = "Language created successfully")
    private String message;

}
