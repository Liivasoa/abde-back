package mg.msys.abde_back.adapter.out.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GutenbergBookDto(
        int id,
        String title,
        List<GutenbergAuthorDto> authors,
        List<String> summaries,
        List<String> languages,
        Map<String, String> formats,
        @JsonProperty("download_count") int downloadCount) {
}
