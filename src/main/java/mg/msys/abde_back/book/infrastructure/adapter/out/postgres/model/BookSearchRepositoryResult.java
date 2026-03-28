package mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model;

import java.time.LocalDate;
import java.util.List;

public record BookSearchRepositoryResult(
        Long id,
        String title,
        LocalDate issued,
        String language,
        List<AuthorReference> authors) {

    public BookSearchRepositoryResult {
        authors = authors == null ? List.of() : List.copyOf(authors);
    }

    public record AuthorReference(Long id, String fullName) {
    }
}