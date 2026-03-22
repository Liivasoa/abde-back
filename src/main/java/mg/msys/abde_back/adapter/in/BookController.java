package mg.msys.abde_back.adapter.in;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.adapter.in.dto.AuthorResponseDto;
import mg.msys.abde_back.adapter.in.dto.BookResponseDto;
import mg.msys.abde_back.adapter.in.dto.ErrorResponse;
import mg.msys.abde_back.adapter.in.dto.PaginatedBookResponseDto;
import mg.msys.abde_back.application.usecase.book.SearchBooksUseCase;
import mg.msys.abde_back.domain.model.BookSearchResult;
import mg.msys.abde_back.domain.model.PaginatedResult;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Search books using optional filters")
public class BookController {

        private final SearchBooksUseCase searchBooksUseCase;

        @GetMapping("/search")
        @Operation(summary = "Search books", description = "Search books by optional publication year, author name, language, and title")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Search executed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedBookResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<PaginatedBookResponseDto> search(
                        @RequestParam(required = false) Integer publicationYear,
                        @RequestParam(required = false) String authorName,
                        @RequestParam(required = false) String language,
                        @RequestParam(required = false) String title,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {

                PaginatedResult<BookSearchResult> result = searchBooksUseCase.execute(
                                publicationYear,
                                authorName,
                                language,
                                title,
                                page,
                                size);

                PaginatedBookResponseDto response = new PaginatedBookResponseDto(
                                result.items().stream()
                                                .map(this::toDto)
                                                .toList(),
                                result.page(),
                                result.size(),
                                result.totalElements(),
                                result.totalPages(),
                                result.hasNext(),
                                result.hasPrevious());

                return ResponseEntity.ok(response);
        }

        private BookResponseDto toDto(BookSearchResult book) {
                return new BookResponseDto(
                                book.id(),
                                book.title(),
                                book.issued(),
                                book.language(),
                                book.authors().stream().map(this::toAuthorDto).toList());
        }

        private AuthorResponseDto toAuthorDto(BookSearchResult.AuthorReference author) {
                return new AuthorResponseDto(author.id(), author.fullName());
        }
}