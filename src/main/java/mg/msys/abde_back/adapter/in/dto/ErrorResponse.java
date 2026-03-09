package mg.msys.abde_back.adapter.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
}
