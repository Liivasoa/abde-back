package mg.msys.abde_back.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Domain] BookSearchResult Tests")
class BookSearchResultTest {

    @Test
    @DisplayName("Should reject null id")
    void shouldRejectNullId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new BookSearchResult(null, "Moby Dick", LocalDate.of(1851, 1, 1), "EN", List.of()));

        assertEquals("Book id cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject null title")
    void shouldRejectNullTitle() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new BookSearchResult(1L, null, LocalDate.of(1851, 1, 1), "EN", List.of()));

        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject blank title")
    void shouldRejectBlankTitle() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new BookSearchResult(1L, "   ", LocalDate.of(1851, 1, 1), "EN", List.of()));

        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should normalize null authors to empty list")
    void shouldNormalizeNullAuthors() {
        BookSearchResult result = new BookSearchResult(1L, "Moby Dick", LocalDate.of(1851, 1, 1), "EN", null);

        assertTrue(result.authors().isEmpty());
    }

    @Test
    @DisplayName("Should make defensive copy for authors list")
    void shouldCopyAuthorsDefensively() {
        List<BookSearchResult.AuthorReference> source = new ArrayList<>();
        source.add(new BookSearchResult.AuthorReference(10L, "Herman Melville"));

        BookSearchResult result = new BookSearchResult(1L, "Moby Dick", LocalDate.of(1851, 1, 1), "EN", source);
        source.clear();

        assertEquals(1, result.authors().size());
    }

    @Test
    @DisplayName("Should expose immutable authors list")
    void shouldExposeImmutableAuthorsList() {
        BookSearchResult result = new BookSearchResult(
                1L,
                "Moby Dick",
                LocalDate.of(1851, 1, 1),
                "EN",
                List.of(new BookSearchResult.AuthorReference(10L, "Herman Melville")));

        assertThrows(UnsupportedOperationException.class,
                () -> result.authors().add(new BookSearchResult.AuthorReference(11L, "George Orwell")));
    }
}