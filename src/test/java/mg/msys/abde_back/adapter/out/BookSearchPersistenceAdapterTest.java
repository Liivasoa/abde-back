package mg.msys.abde_back.adapter.out;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import mg.msys.abde_back.application.port.BookSearchPersistencePort;
import mg.msys.abde_back.domain.model.Book;
import mg.msys.abde_back.domain.model.BookSearchCriteria;
import mg.msys.abde_back.domain.model.PaginatedResult;
import mg.msys.abde_back.infrastructure.repository.BookSearchJpaRepository;

@DisplayName("[Adapter/Out] Book Search Persistence Adapter Tests")
class BookSearchPersistenceAdapterTest extends AbstractAdapterTest {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private BookSearchPersistencePort bookSearchPersistencePort;

    @BeforeEach
    void setUp() {
        this.bookSearchPersistencePort = new BookSearchPersistenceAdapter(
                new BookSearchJpaRepository(namedParameterJdbcTemplate));

        jdbcTemplate.update("INSERT INTO book(id, issued, title, languages, subjects) VALUES (1, ?, ?, ?, ?)",
                LocalDate.of(1851, 1, 1), "Moby Dick", "EN", "Sea");
        jdbcTemplate.update("INSERT INTO book(id, issued, title, languages, subjects) VALUES (2, ?, ?, ?, ?)",
                LocalDate.of(1949, 6, 8), "Nineteen Eighty-Four", "EN", "Dystopia");
        jdbcTemplate.update("INSERT INTO book(id, issued, title, languages, subjects) VALUES (3, ?, ?, ?, ?)",
                LocalDate.of(1932, 1, 1), "Le Petit Prince", "FR", "Children");

        jdbcTemplate.update(
                "INSERT INTO author(id, last_name, first_names, birth_year, death_year, normalized_key) VALUES (10, ?, ?, ?, ?, ?)",
                "Melville", "Herman", 1819, 1891, "melville_herman");
        jdbcTemplate.update(
                "INSERT INTO author(id, last_name, first_names, birth_year, death_year, normalized_key) VALUES (11, ?, ?, ?, ?, ?)",
                "Orwell", "George", 1903, 1950, "orwell_george");
        jdbcTemplate.update(
                "INSERT INTO author(id, last_name, first_names, birth_year, death_year, normalized_key) VALUES (12, ?, ?, ?, ?, ?)",
                "de Saint-Exupery", "Antoine", 1900, 1944, "de_saint_exupery_antoine");

        jdbcTemplate.update("INSERT INTO book_author(book_id, author_id) VALUES (1, 10)");
        jdbcTemplate.update("INSERT INTO book_author(book_id, author_id) VALUES (2, 11)");
        jdbcTemplate.update("INSERT INTO book_author(book_id, author_id) VALUES (3, 12)");
    }

    @Test
    @DisplayName("Should return paginated books when no filter is provided")
    void testSearchWithoutFilters() {
        PaginatedResult<Book> result = bookSearchPersistencePort
                .searchPage(new BookSearchCriteria(null, null, null, null, 0, 2));

        assertEquals(2, result.items().size());
        assertEquals(3, result.totalElements());
        assertEquals(2, result.totalPages());
    }

    @Test
    @DisplayName("Should filter by publication year with pagination")
    void testSearchByPublicationYear() {
        PaginatedResult<Book> result = bookSearchPersistencePort
                .searchPage(new BookSearchCriteria(1949, null, null, null, 0, 10));

        assertEquals(1, result.items().size());
        assertEquals("Nineteen Eighty-Four", result.items().get(0).title());
    }

    @Test
    @DisplayName("Should filter by author name with pagination")
    void testSearchByAuthorName() {
        PaginatedResult<Book> result = bookSearchPersistencePort
                .searchPage(new BookSearchCriteria(null, "melville", null, null, 0, 10));

        assertEquals(1, result.items().size());
        assertEquals("Moby Dick", result.items().get(0).title());
        assertTrue(result.items().get(0).authors().contains("Herman Melville"));
    }

    @Test
    @DisplayName("Should combine filters with AND semantics")
    void testSearchByCombinedFilters() {
        PaginatedResult<Book> result = bookSearchPersistencePort
                .searchPage(new BookSearchCriteria(1851, null, "EN", "Moby", 0, 10));

        assertEquals(1, result.items().size());
        assertEquals("Moby Dick", result.items().get(0).title());

        PaginatedResult<Book> emptyResult = bookSearchPersistencePort
                .searchPage(new BookSearchCriteria(1851, null, "FR", "Moby", 0, 10));
        assertTrue(emptyResult.items().isEmpty());
    }

    @Test
    @DisplayName("Should declare adapter-level pagination method")
    void testAdapterDeclaresSearchPageMethod() {
        assertTrue(hasDeclaredSearchPageMethod());
    }

    @Test
    @DisplayName("Should delegate paginated search to infrastructure repository")
    void testPaginationDelegatesToInfrastructureRepository() {
        assertTrue(hasConstructorWithBookSearchJpaRepositoryParameter());
    }

    @Test
    @DisplayName("Should not keep SQL query constants in adapter for pagination path")
    void testPaginationSqlShouldNotLiveInAdapter() {
        assertFalse(hasDeclaredField("SEARCH_PAGE_SQL"));
        assertFalse(hasDeclaredField("COUNT_SQL"));
    }

    private boolean hasDeclaredSearchPageMethod() {
        try {
            BookSearchPersistenceAdapter.class.getDeclaredMethod("searchPage", BookSearchCriteria.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private boolean hasConstructorWithBookSearchJpaRepositoryParameter() {
        try {
            Class<?> repositoryType = Class.forName(
                    "mg.msys.abde_back.infrastructure.repository.BookSearchJpaRepository");
            Constructor<?> constructor = BookSearchPersistenceAdapter.class.getDeclaredConstructor(repositoryType);
            return constructor.getParameterCount() == 1;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    private boolean hasDeclaredField(String fieldName) {
        try {
            BookSearchPersistenceAdapter.class.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}