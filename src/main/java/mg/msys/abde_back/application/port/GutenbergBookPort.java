package mg.msys.abde_back.application.port;

import mg.msys.abde_back.domain.model.Book;

import java.util.List;

public interface GutenbergBookPort {

    List<Book> fetchBooks(int page, String languages);
}
