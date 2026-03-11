package mg.msys.abde_back.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.adapter.in.dto.AuthorResponse;
import mg.msys.abde_back.adapter.in.dto.BookResponse;
import mg.msys.abde_back.application.usecase.book.GetBooksUseCase;
import mg.msys.abde_back.domain.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Retrieve books from Project Gutenberg")
public class BookController {

        private final GetBooksUseCase getBooksUseCase;

        @GetMapping
        @Operation(summary = "Get books from Project Gutenberg", description = "Returns a paginated list of books with authors, summaries, languages and download links")
        public ResponseEntity<List<BookResponse>> getBooks(
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(required = false) String languages) {

                List<BookResponse> books = getBooksUseCase.execute(page, languages).stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(books);
        }

        private BookResponse toResponse(Book book) {
                List<AuthorResponse> authors = book.getAuthors().stream()
                                .map(a -> new AuthorResponse(a.getName(), a.getBirthYear(), a.getDeathYear()))
                                .toList();

                return new BookResponse(
                                book.getId(),
                                book.getTitle(),
                                book.getSummaries(),
                                book.getLanguages(),
                                book.getFormats(),
                                authors,
                                book.getDownloadCount());
        }
}
