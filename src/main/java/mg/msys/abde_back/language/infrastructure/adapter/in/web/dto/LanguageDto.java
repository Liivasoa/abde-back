package mg.msys.abde_back.language.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LanguageDto {
    private String code;
    private String label;
}
