package mg.msys.abde_back.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import mg.msys.abde_back.domain.model.BookSearchResult;

@DisplayName("[Repository] Book Search JPA Repository - RowMapper Tests")
class BookSearchJpaRepositoryTest {

    private RowMapper<BookSearchResult> rowMapper;
    private ResultSet resultSet;
    private BookSearchJpaRepository repository;

    @BeforeEach
    void setUp() {
        resultSet = mock(ResultSet.class);
        repository = new BookSearchJpaRepository(mock(NamedParameterJdbcTemplate.class));
    }

    @Test
    @DisplayName("Should create BookSearchResult with AuthorReference list when result set contains author data")
    void testRowMapperCreatesBookSearchResultWithAuthorReferences() throws Exception {
        // Arrange
        Long[] authorIds = { 10L, 11L };
        String[] authorNames = { "Herman Melville", "George Orwell" };
        Array authorIdsArray = mockArray(authorIds);
        Array authorNamesArray = mockArray(authorNames);

        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("title")).thenReturn("Moby Dick");
        when(resultSet.getDate("issued")).thenReturn(new java.sql.Date(
                java.sql.Date.valueOf(LocalDate.of(1851, 1, 1)).getTime()));
        when(resultSet.getString("languages")).thenReturn("EN");
        when(resultSet.getArray("author_ids")).thenReturn(authorIdsArray);
        when(resultSet.getArray("author_names")).thenReturn(authorNamesArray);

        rowMapper = invokeBookRowMapper();
        BookSearchResult result = rowMapper.mapRow(resultSet, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Moby Dick", result.title());
        assertEquals(LocalDate.of(1851, 1, 1), result.issued());
        assertEquals("EN", result.language());
        assertThat(result.authors())
                .hasSizeGreaterThan(0)
                .allMatch(author -> author.id() != null && !author.fullName().isEmpty());
        assertThat(result.authors())
                .extracting(BookSearchResult.AuthorReference::id)
                .containsExactly(10L, 11L);
        assertThat(result.authors())
                .extracting(BookSearchResult.AuthorReference::fullName)
                .containsExactly("Herman Melville", "George Orwell");
    }

    @Test
    @DisplayName("Should return empty author list when result set has no authors")
    void testRowMapperHandlesEmptyAuthorList() throws Exception {
        // Arrange
        when(resultSet.getLong("id")).thenReturn(2L);
        when(resultSet.getString("title")).thenReturn("Unknown Book");
        when(resultSet.getDate("issued")).thenReturn(new java.sql.Date(
                java.sql.Date.valueOf(LocalDate.of(2024, 1, 1)).getTime()));
        when(resultSet.getString("languages")).thenReturn("EN");
        when(resultSet.getArray("author_ids")).thenReturn(null);
        when(resultSet.getArray("author_names")).thenReturn(null);

        rowMapper = invokeBookRowMapper();
        BookSearchResult result = rowMapper.mapRow(resultSet, 1);

        // Assert
        assertNotNull(result);
        assertNotNull(result.authors());
        assertTrue(result.authors().isEmpty());
    }

    @Test
    @DisplayName("Should handle null issued date without throwing exception")
    void testRowMapperHandlesNullIssuedDate() throws Exception {
        // Arrange
        Long[] authorIds = { 12L };
        String[] authorNames = { "Antoine de Saint-Exupéry" };
        Array authorIdsArray = mockArray(authorIds);
        Array authorNamesArray = mockArray(authorNames);

        when(resultSet.getLong("id")).thenReturn(3L);
        when(resultSet.getString("title")).thenReturn("Le Petit Prince");
        when(resultSet.getDate("issued")).thenReturn(null);
        when(resultSet.getString("languages")).thenReturn("FR");
        when(resultSet.getArray("author_ids")).thenReturn(authorIdsArray);
        when(resultSet.getArray("author_names")).thenReturn(authorNamesArray);

        rowMapper = invokeBookRowMapper();
        BookSearchResult result = rowMapper.mapRow(resultSet, 1);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.id());
        assertEquals("Le Petit Prince", result.title());
        assertTrue(result.issued() == null);
        assertEquals("FR", result.language());
        assertEquals(1, result.authors().size());
    }

    @Test
    @DisplayName("Should validate BookSearchResult invariants")
    void testRowMapperValidatesBookSearchResultInvariants() throws Exception {
        // Arrange
        Long[] authorIds = { 10L };
        String[] authorNames = { "Herman Melville" };
        Array authorIdsArray = mockArray(authorIds);
        Array authorNamesArray = mockArray(authorNames);

        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("title")).thenReturn("Moby Dick");
        when(resultSet.getDate("issued")).thenReturn(new java.sql.Date(
                java.sql.Date.valueOf(LocalDate.of(1851, 1, 1)).getTime()));
        when(resultSet.getString("languages")).thenReturn("EN");
        when(resultSet.getArray("author_ids")).thenReturn(authorIdsArray);
        when(resultSet.getArray("author_names")).thenReturn(authorNamesArray);

        rowMapper = invokeBookRowMapper();
        BookSearchResult result = rowMapper.mapRow(resultSet, 1);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertNotNull(result.title());
        assertTrue(!result.title().isEmpty());
        assertNotNull(result.authors());
    }

    @Test
    @DisplayName("Should correctly match author ids with author names in correct order")
    void testRowMapperMatchesAuthorIdsWithNames() throws Exception {
        // Arrange
        Long[] authorIds = { 100L, 101L, 102L };
        String[] authorNames = { "Author One", "Author Two", "Author Three" };
        Array authorIdsArray = mockArray(authorIds);
        Array authorNamesArray = mockArray(authorNames);

        when(resultSet.getLong("id")).thenReturn(99L);
        when(resultSet.getString("title")).thenReturn("Multi-Author Book");
        when(resultSet.getDate("issued")).thenReturn(new java.sql.Date(
                java.sql.Date.valueOf(LocalDate.of(2020, 6, 15)).getTime()));
        when(resultSet.getString("languages")).thenReturn("EN");
        when(resultSet.getArray("author_ids")).thenReturn(authorIdsArray);
        when(resultSet.getArray("author_names")).thenReturn(authorNamesArray);

        rowMapper = invokeBookRowMapper();
        BookSearchResult result = rowMapper.mapRow(resultSet, 1);

        // Assert
        assertThat(result.authors()).hasSize(3);
        for (int i = 0; i < 3; i++) {
            assertEquals(authorIds[i], result.authors().get(i).id());
            assertEquals(authorNames[i], result.authors().get(i).fullName());
        }
    }

    @Test
    @DisplayName("Should keep only valid author pairs and handle mismatched arrays")
    void testToAuthorReferencesFiltersInvalidEntries() throws Exception {
        Array authorIdsArray = mockArray(new Object[] { 1L, null, 3L, 4L });
        Array authorNamesArray = mockArray(new Object[] { "Author One", " ", "", "Author Four" });

        List<BookSearchResult.AuthorReference> authors = invokeToAuthorReferences(authorIdsArray, authorNamesArray);

        assertThat(authors).hasSize(2);
        assertThat(authors)
                .extracting(BookSearchResult.AuthorReference::id)
                .containsExactly(1L, 4L);
        assertThat(authors)
                .extracting(BookSearchResult.AuthorReference::fullName)
                .containsExactly("Author One", "Author Four");
    }

    @Test
    @DisplayName("Should return empty list when id or name arrays are empty")
    void testToAuthorReferencesReturnsEmptyForEmptyArrays() throws Exception {
        Array idsEmpty = mockArray(new Object[] {});
        Array namesEmpty = mockArray(new Object[] {});

        List<BookSearchResult.AuthorReference> authors = invokeToAuthorReferences(idsEmpty, namesEmpty);

        assertThat(authors).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when ids array is null but names array exists")
    void testToAuthorReferencesWithNullIdsArrayOnly() throws Exception {
        Array names = mockArray(new Object[] { "Author" });

        List<BookSearchResult.AuthorReference> authors = invokeToAuthorReferences(null, names);

        assertThat(authors).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when names array is null but ids array exists")
    void testToAuthorReferencesWithNullNamesArrayOnly() throws Exception {
        Array ids = mockArray(new Object[] { 1L });

        List<BookSearchResult.AuthorReference> authors = invokeToAuthorReferences(ids, null);

        assertThat(authors).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when underlying ids array content is null")
    void testToAuthorReferencesWithNullIdValues() throws Exception {
        Array ids = mockArray(null);
        Array names = mockArray(new Object[] { "Author" });

        List<BookSearchResult.AuthorReference> authors = invokeToAuthorReferences(ids, names);

        assertThat(authors).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when underlying names array content is null")
    void testToAuthorReferencesWithNullNameValues() throws Exception {
        Array ids = mockArray(new Object[] { 1L });
        Array names = mockArray(null);

        List<BookSearchResult.AuthorReference> authors = invokeToAuthorReferences(ids, names);

        assertThat(authors).isEmpty();
    }

    @Test
    @DisplayName("Should ignore entries with null fullName")
    void testToAuthorReferencesIgnoresNullFullName() throws Exception {
        Array ids = mockArray(new Object[] { 1L, 2L });
        Array names = mockArray(new Object[] { null, "Author Two" });

        List<BookSearchResult.AuthorReference> authors = invokeToAuthorReferences(ids, names);

        assertThat(authors).hasSize(1);
        assertEquals(2L, authors.get(0).id());
        assertEquals("Author Two", authors.get(0).fullName());
    }

    @Test
    @DisplayName("Should convert numbers and strings to Long and keep null as null")
    void testToLongCoversAllBranches() throws Exception {
        assertNull(invokeToLong(null));
        assertEquals(42L, invokeToLong(42));
        assertEquals(99L, invokeToLong("99"));
    }

    @SuppressWarnings("unchecked")
    private RowMapper<BookSearchResult> invokeBookRowMapper()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = BookSearchJpaRepository.class.getDeclaredMethod("bookRowMapper");
        method.setAccessible(true);
        return (RowMapper<BookSearchResult>) method.invoke(repository);
    }

    @SuppressWarnings("unchecked")
    private List<BookSearchResult.AuthorReference> invokeToAuthorReferences(Array authorIdsArray,
            Array authorNamesArray)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = BookSearchJpaRepository.class.getDeclaredMethod("toAuthorReferences", Array.class, Array.class);
        method.setAccessible(true);
        return (List<BookSearchResult.AuthorReference>) method.invoke(repository, authorIdsArray, authorNamesArray);
    }

    private Long invokeToLong(Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = BookSearchJpaRepository.class.getDeclaredMethod("toLong", Object.class);
        method.setAccessible(true);
        return (Long) method.invoke(repository, value);
    }

    @SuppressWarnings("unchecked")
    private Array mockArray(Object[] values) throws SQLException {
        Array array = mock(Array.class);
        when(array.getArray()).thenReturn(values);
        return array;
    }
}
