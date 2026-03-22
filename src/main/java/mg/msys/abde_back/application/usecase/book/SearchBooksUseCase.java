package mg.msys.abde_back.application.usecase.book;

import java.util.List;

import mg.msys.abde_back.domain.model.PaginatedResult;
import mg.msys.abde_back.domain.model.BookSearchResult;

public interface SearchBooksUseCase {

    List<BookSearchResult> execute(Integer publicationYear, String authorName, String language, String title);

    PaginatedResult<BookSearchResult> execute(Integer publicationYear, String authorName, String language, String title,
            int page,
            int size);
}