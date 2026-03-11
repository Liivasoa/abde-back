package mg.msys.abde_back.adapter.out.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GutenbergPageDto(
                int count,
                String next,
                List<GutenbergBookDto> results) {
}
