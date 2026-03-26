package mg.msys.abde_back.book.infrastructure.adapter.out.postgres;

import static org.assertj.core.api.Assertions.assertThat;
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

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.book.application.port.out.BookPersistencePort;
import mg.msys.abde_back.domain.model.PaginatedResult;
import mg.msys.abde_back.shared.infrastructure.adapter.out.postgres.AbstractAdapterTest;

@DisplayName("[Adapter/Out] Book Search Persistence Adapter Tests")
class BookPersistenceAdapterTest extends AbstractAdapterTest {

        @Autowired
        private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        private BookPersistencePort bookSearchPersistencePort;

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
                jdbcTemplate.update("INSERT INTO book(id, issued, title, languages, subjects) VALUES (4, ?, ?, ?, ?)",
                                LocalDate.of(2025, 1, 1), "No Author Book", "EN", "Test");
                jdbcTemplate.update("INSERT INTO book(id, issued, title, languages, subjects) VALUES (5, ?, ?, ?, ?)",
                                LocalDate.of(2026, 1, 1), "Nameless Author Book", "EN", "Test");

                jdbcTemplate.update(
                                "INSERT INTO author(id, last_name, first_names, birth_year, death_year, normalized_key) VALUES (10, ?, ?, ?, ?, ?)",
                                "Melville", "Herman", 1819, 1891, "melville_herman");
                jdbcTemplate.update(
                                "INSERT INTO author(id, last_name, first_names, birth_year, death_year, normalized_key) VALUES (11, ?, ?, ?, ?, ?)",
                                "Orwell", "George", 1903, 1950, "orwell_george");
                jdbcTemplate.update(
                                "INSERT INTO author(id, last_name, first_names, birth_year, death_year, normalized_key) VALUES (12, ?, ?, ?, ?, ?)",
                                "de Saint-Exupery", "Antoine", 1900, 1944, "de_saint_exupery_antoine");
                jdbcTemplate.update(
                                "INSERT INTO author(id, last_name, first_names, birth_year, death_year, normalized_key) VALUES (13, ?, ?, ?, ?, ?)",
                                "", "", 1900, 1944, "blank_blank");

                jdbcTemplate.update("INSERT INTO book_author(book_id, author_id) VALUES (1, 10)");
                jdbcTemplate.update("INSERT INTO book_author(book_id, author_id) VALUES (2, 11)");
                jdbcTemplate.update("INSERT INTO book_author(book_id, author_id) VALUES (3, 12)");
                jdbcTemplate.update("INSERT INTO book_author(book_id, author_id) VALUES (5, 13)");
        }

        @Test
        @DisplayName("Should return non paginated search results")
        void testSearchWithoutPagination() {
                List<BookSearchResult> result = bookSearchPersistencePort
                                .search(new BookSearchCriteria(null, null, null, null));

                assertThat(result).hasSize(5);
                assertThat(result)
                                .allMatch(book -> book.id() != null)
                                .allMatch(book -> book.title() != null && !book.title().isBlank());
        }

        @Test
        @DisplayName("Should return paginated book results when no filter is provided")
        void testSearchWithoutFilters() {
                PaginatedResult<BookSearchResult> result = bookSearchPersistencePort
                                .searchPage(new BookSearchCriteria(null, null, null, null, 0, 2));

                assertEquals(2, result.items().size());
                assertEquals(5, result.totalElements());
                assertEquals(3, result.totalPages());
                assertThat(result.items()).allMatch(item -> item.authors() != null);
        }

        @Test
        @DisplayName("Should filter by publication year with pagination and return BookSearchResult")
        void testSearchByPublicationYear() {
                PaginatedResult<BookSearchResult> result = bookSearchPersistencePort
                                .searchPage(new BookSearchCriteria(1949, null, null, null, 0, 10));

                assertEquals(1, result.items().size());
                BookSearchResult firstResult = result.items().get(0);
                assertEquals("Nineteen Eighty-Four", firstResult.title());
                assertThat(firstResult.authors())
                                .hasSizeGreaterThan(0)
                                .allMatch(author -> author.id() != null && !author.fullName().isEmpty());
        }

        @Test
        @DisplayName("Should filter by author name with pagination and return BookSearchResult with AuthorReference")
        void testSearchByAuthorName() {
                PaginatedResult<BookSearchResult> result = bookSearchPersistencePort
                                .searchPage(new BookSearchCriteria(null, "melville", null, null, 0, 10));

                assertEquals(1, result.items().size());
                BookSearchResult firstResult = result.items().get(0);
                assertEquals("Moby Dick", firstResult.title());
                assertThat(firstResult.authors())
                                .hasSizeGreaterThan(0)
                                .anyMatch(author -> author.fullName().contains("Melville"));
        }

        @Test
        @DisplayName("Should combine filters with AND semantics and return BookSearchResult")
        void testSearchByCombinedFilters() {
                PaginatedResult<BookSearchResult> result = bookSearchPersistencePort
                                .searchPage(new BookSearchCriteria(1851, null, "EN", "Moby", 0, 10));

                assertEquals(1, result.items().size());
                BookSearchResult firstResult = result.items().get(0);
                assertEquals("Moby Dick", firstResult.title());
                assertEquals(10L, firstResult.authors().get(0).id());

                PaginatedResult<BookSearchResult> emptyResult = bookSearchPersistencePort
                                .searchPage(new BookSearchCriteria(1851, null, "FR", "Moby", 0, 10));
                assertTrue(emptyResult.items().isEmpty());
        }

        @Test
        @DisplayName("Should return BookSearchResult with author references containing id and fullName")
        void testSearchResultContainsAuthorReferences() {
                PaginatedResult<BookSearchResult> result = bookSearchPersistencePort
                                .searchPage(new BookSearchCriteria(null, null, null, null, 0, 10));

                assertThat(result.items()).isNotEmpty();
                BookSearchResult firstResult = result.items().get(0);

                assertThat(firstResult.authors()).allMatch(
                                author -> author.id() != null,
                                "All AuthorReference must have non-null id").allMatch(
                                                author -> author.fullName() != null && !author.fullName().isEmpty(),
                                                "All AuthorReference must have non-empty fullName");
        }

        @Test
        @DisplayName("Should return empty author list for books without author relation")
        void testSearchIncludesBookWithoutAuthors() {
                List<BookSearchResult> result = bookSearchPersistencePort
                                .search(new BookSearchCriteria(null, null, null, null));

                BookSearchResult noAuthorBook = result.stream()
                                .filter(book -> "No Author Book".equals(book.title()))
                                .findFirst()
                                .orElseThrow();

                assertThat(noAuthorBook.authors()).isEmpty();
        }

        @Test
        @DisplayName("Should filter out blank author full name entries")
        void testSearchFiltersBlankAuthorFullName() {
                List<BookSearchResult> result = bookSearchPersistencePort
                                .search(new BookSearchCriteria(null, null, null, "Nameless", 0, 10));

                assertThat(result).hasSize(1);
                assertThat(result.get(0).title()).isEqualTo("Nameless Author Book");
                assertThat(result.get(0).authors()).isEmpty();
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
                        Constructor<?> constructor = BookSearchPersistenceAdapter.class
                                        .getDeclaredConstructor(repositoryType);
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