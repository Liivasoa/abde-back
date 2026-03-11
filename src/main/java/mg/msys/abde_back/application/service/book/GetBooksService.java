package mg.msys.abde_back.application.service.book;

import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.application.port.GutenbergBookPort;
import mg.msys.abde_back.application.usecase.book.GetBooksUseCase;
import mg.msys.abde_back.domain.model.Book;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetBooksService implements GetBooksUseCase {

    private final GutenbergBookPort gutenbergBookPort;

    @Override
    public List<Book> execute(int page, String languages) {
        return gutenbergBookPort.fetchBooks(page, languages);
    }
}
