package mg.msys.abde_back.book.application.port.in.query;

import java.util.List;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.domain.model.PaginatedResult;

public interface SearchBookUseCase {

    List<BookSearchResult> execute(Integer publicationYear, String authorName, String language, String title);

    PaginatedResult<BookSearchResult> execute(Integer publicationYear, String authorName, String language, String title,
            int page,
            int size);
}