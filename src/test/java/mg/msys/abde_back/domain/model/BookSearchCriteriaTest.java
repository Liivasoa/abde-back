package mg.msys.abde_back.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;

@DisplayName("[Domain/Model] Book Search Criteria Tests")
class BookSearchCriteriaTest {

    @Test
    @DisplayName("Should keep pagination parameters when valid")
    void shouldKeepPaginationParametersWhenValid() {
        BookSearchCriteria criteria = new BookSearchCriteria(1851, "Melville", "EN", "Moby", 2, 10);

        assertEquals(2, criteria.page());
        assertEquals(10, criteria.size());
        assertEquals(20, criteria.offset());
    }

    @Test
    @DisplayName("Should reject negative page")
    void shouldRejectNegativePage() {
        assertThrows(IllegalArgumentException.class,
                () -> new BookSearchCriteria(1851, "Melville", "EN", "Moby", -1, 10));
    }

    @Test
    @DisplayName("Should reject invalid size")
    void shouldRejectInvalidSize() {
        assertThrows(IllegalArgumentException.class,
                () -> new BookSearchCriteria(1851, "Melville", "EN", "Moby", 0, 0));
    }
}
