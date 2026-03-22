package mg.msys.abde_back.application.usecase.book;

import java.util.List;

import mg.msys.abde_back.domain.model.Book;
import mg.msys.abde_back.domain.model.PaginatedResult;

public interface SearchBooksUseCase {

    List<Book> execute(Integer publicationYear, String authorName, String language, String title);

    PaginatedResult<Book> execute(Integer publicationYear, String authorName, String language, String title, int page,
            int size);
}