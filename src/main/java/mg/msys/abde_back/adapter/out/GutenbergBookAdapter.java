package mg.msys.abde_back.adapter.out;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mg.msys.abde_back.adapter.out.dto.GutenbergPageDto;
import mg.msys.abde_back.adapter.out.mapper.GutenbergBookMapper;
import mg.msys.abde_back.application.port.GutenbergBookPort;
import mg.msys.abde_back.domain.model.Book;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GutenbergBookAdapter implements GutenbergBookPort {

    private final RestClient gutenbergRestClient;
    private final GutenbergBookMapper gutenbergBookMapper;

    @Override
    public List<Book> fetchBooks(int page, String languages) {
        log.info("Fetching books from Gutendex, page={}, languages={}", page, languages);

        GutenbergPageDto response = gutenbergRestClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/books/").queryParam("page", page);
                    if (languages != null && !languages.isBlank()) {
                        builder = builder.queryParam("languages", languages);
                    }
                    return builder.build();
                })
                .retrieve()
                .body(GutenbergPageDto.class);

        if (response == null) {
            log.warn("Gutendex returned null response for page={}", page);
            return List.of();
        }
        if (response.results() == null || response.results().isEmpty()) {
            log.warn("Gutendex returned empty results for page={}", page);
            return List.of();
        }

        log.info("Gutendex returned {} books", response.results().size());
        return response.results().stream()
                .map(gutenbergBookMapper::toDomain)
                .toList();
    }
}
