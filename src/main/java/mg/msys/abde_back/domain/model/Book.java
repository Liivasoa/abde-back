package mg.msys.abde_back.domain.model;

import java.time.LocalDate;
import java.util.List;

public record Book(Long id, String title, LocalDate issued, String language, List<String> authors) {

    public Book {
        if (id == null) {
            throw new IllegalArgumentException("Book id cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be null or empty");
        }
        authors = authors == null ? List.of() : List.copyOf(authors);
    }
}