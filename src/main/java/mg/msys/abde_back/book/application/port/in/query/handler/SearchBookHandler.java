package mg.msys.abde_back.book.application.port.in.query.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.book.application.port.in.query.SearchBookUseCase;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.book.application.port.out.BookPersistencePort;
import mg.msys.abde_back.shared.application.port.in.query.dto.PaginatedResult;

@Component
@RequiredArgsConstructor
public class SearchBookHandler implements SearchBookUseCase {

    private final BookPersistencePort bookSearchPersistencePort;

    @Override
    public PaginatedResult<BookSearchResult> execute(Integer publicationYear, String authorName, String language,
            String title,
            int page,
            int size) {
        return bookSearchPersistencePort
                .search(new BookSearchCriteria(publicationYear, authorName, language, title, page, size));
    }
}