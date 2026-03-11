package mg.msys.abde_back.application.usecase.book;

import mg.msys.abde_back.domain.model.Book;

import java.util.List;

public interface GetBooksUseCase {

    List<Book> execute(int page, String languages);
}
