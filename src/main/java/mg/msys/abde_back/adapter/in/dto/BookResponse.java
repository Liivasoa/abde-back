package mg.msys.abde_back.adapter.in.dto;

import java.util.List;
import java.util.Map;

public record BookResponse(
                int id,
                String title,
                List<String> summaries,
                List<String> languages,
                Map<String, String> formats,
                List<AuthorResponse> authors,
                int downloadCount) {
}
