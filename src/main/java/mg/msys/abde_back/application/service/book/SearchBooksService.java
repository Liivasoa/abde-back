package mg.msys.abde_back.application.service.book;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.application.port.BookSearchPersistencePort;
import mg.msys.abde_back.application.usecase.book.SearchBooksUseCase;
import mg.msys.abde_back.domain.model.BookSearchCriteria;
import mg.msys.abde_back.domain.model.BookSearchResult;
import mg.msys.abde_back.domain.model.PaginatedResult;

@Component
@RequiredArgsConstructor
public class SearchBooksService implements SearchBooksUseCase {

    private final BookSearchPersistencePort bookSearchPersistencePort;

    @Override
    public List<BookSearchResult> execute(Integer publicationYear, String authorName, String language, String title) {
        return bookSearchPersistencePort.search(toCriteria(publicationYear, authorName, language, title));
    }

    @Override
    public PaginatedResult<BookSearchResult> execute(Integer publicationYear, String authorName, String language,
            String title,
            int page,
            int size) {
        return bookSearchPersistencePort
                .searchPage(toCriteria(publicationYear, authorName, language, title, page, size));
    }

    private BookSearchCriteria toCriteria(Integer publicationYear, String authorName, String language, String title) {
        return new BookSearchCriteria(publicationYear, authorName, language, title);
    }

    private BookSearchCriteria toCriteria(Integer publicationYear, String authorName, String language, String title,
            int page, int size) {
        return new BookSearchCriteria(publicationYear, authorName, language, title, page, size);
    }
}