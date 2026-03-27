package mg.msys.abde_back.book.application.port.in.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.book.application.port.in.query.handler.SearchBookHandler;
import mg.msys.abde_back.book.application.port.out.BookPersistencePortFake;
import mg.msys.abde_back.shared.application.port.in.query.dto.PaginatedResult;

@DisplayName("[Application/Use case] Search Books Use Case Tests")
class SearchBookUseCaseTest {

    private SearchBookUseCase useCase;
    private BookPersistencePortFake fakePort;

    @BeforeEach
    void setUp() {
        fakePort = new BookPersistencePortFake();
        useCase = new SearchBookHandler(fakePort);
    }

    @Test
    @DisplayName("Should call port with all provided filters")
    void testExecuteWithAllFilters() {
        fakePort.setBooksToReturn(
                List.of(new BookSearchResult(1L, "Moby Dick", LocalDate.of(1851, 1, 1), "EN",
                        List.of(new BookSearchResult.AuthorReference(10L, "Herman Melville")))));
        fakePort.setTotalElementsToReturn(41L);

        PaginatedResult<BookSearchResult> result = useCase.execute(1851, "Melville", "EN", "Moby", 2, 10);

        assertEquals(1, fakePort.getCallCount());
        BookSearchCriteria criteria = fakePort.getLastCriteria();
        assertNotNull(criteria);
        assertEquals(1851, criteria.publicationYear());
        assertEquals("Melville", criteria.authorName());
        assertEquals("EN", criteria.language());
        assertEquals("Moby", criteria.title());
        assertEquals(2, criteria.page());
        assertEquals(10, criteria.size());
        assertEquals(1, result.items().size());
        assertEquals(41L, result.totalElements());
    }

    @Test
    @DisplayName("Should normalize blank filters to null")
    void testExecuteNormalizesBlankStrings() {
        useCase.execute(null, "  ", "\t", "", 0, 20);

        BookSearchCriteria criteria = fakePort.getLastCriteria();
        assertNotNull(criteria);
        assertEquals(null, criteria.authorName());
        assertEquals(null, criteria.language());
        assertEquals(null, criteria.title());
        assertEquals(0, criteria.page());
        assertEquals(20, criteria.size());
        assertEquals(1, fakePort.getSearchCallCount());
    }

    @Test
    @DisplayName("Should return paginated result from port")
    void testExecuteReturnsPaginatedResult() {
        fakePort.setBooksToReturn(
                List.of(new BookSearchResult(1L, "Moby Dick", LocalDate.of(1851, 1, 1), "EN",
                        List.of(new BookSearchResult.AuthorReference(10L, "Herman Melville")))));
        fakePort.setTotalElementsToReturn(7L);

        PaginatedResult<BookSearchResult> result = useCase.execute(null, null, null, null, 1, 5);

        assertEquals(1, result.page());
        assertEquals(5, result.size());
        assertEquals(7L, result.totalElements());
        assertEquals(2, result.totalPages());
        assertEquals(false, result.hasNext());
        assertEquals(true, result.hasPrevious());
        assertEquals(1, result.items().size());
    }

    @Test
    @DisplayName("Should reject invalid publication year")
    void testRejectInvalidYear() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(10000, null, null, null, 0, 20));
        assertEquals(0, fakePort.getCallCount());
    }
}