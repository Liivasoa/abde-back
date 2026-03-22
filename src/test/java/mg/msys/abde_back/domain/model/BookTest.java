package mg.msys.abde_back.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Domain/Model] Book Tests")
class BookTest {

    @Test
    @DisplayName("Should reject null id")
    void shouldRejectNullId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Book(null, "Moby Dick", LocalDate.of(1851, 1, 1), "EN", List.of("Herman Melville")));

        assertEquals("Book id cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject null title")
    void shouldRejectNullTitle() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Book(1L, null, LocalDate.of(1851, 1, 1), "EN", List.of("Herman Melville")));

        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject blank title")
    void shouldRejectBlankTitle() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Book(1L, "   ", LocalDate.of(1851, 1, 1), "EN", List.of("Herman Melville")));

        assertEquals("Book title cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should normalize null authors to empty list")
    void shouldNormalizeNullAuthors() {
        Book result = new Book(1L, "Moby Dick", LocalDate.of(1851, 1, 1), "EN", null);

        assertTrue(result.authors().isEmpty());
    }

    @Test
    @DisplayName("Should make defensive copy of authors")
    void shouldMakeDefensiveCopy() {
        List<String> sourceAuthors = new ArrayList<>();
        sourceAuthors.add("Herman Melville");

        Book result = new Book(1L, "Moby Dick", LocalDate.of(1851, 1, 1), "EN", sourceAuthors);
        sourceAuthors.clear();

        assertEquals(1, result.authors().size());
        assertEquals("Herman Melville", result.authors().get(0));
    }

    @Test
    @DisplayName("Should expose immutable authors list")
    void shouldExposeImmutableAuthorsList() {
        Book result = new Book(1L, "Moby Dick", LocalDate.of(1851, 1, 1), "EN", List.of("Herman Melville"));

        assertThrows(UnsupportedOperationException.class, () -> result.authors().add("George Orwell"));
    }
}