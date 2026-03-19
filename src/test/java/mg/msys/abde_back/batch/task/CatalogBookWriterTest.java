package mg.msys.abde_back.batch.task;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;

import mg.msys.abde_back.batch.entity.BookAuthorLink;
import mg.msys.abde_back.batch.entity.GutenbergBook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CatalogBookWriterTest {

    @Test
    void shouldInsertOnlyResolvedBookAuthorLinks() throws Exception {
        @SuppressWarnings("unchecked")
        JdbcBatchItemWriter<GutenbergBook> bookWriter = (JdbcBatchItemWriter<GutenbergBook>) mock(
                JdbcBatchItemWriter.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        CatalogBookWriter writer = new CatalogBookWriter(bookWriter, jdbcTemplate);

        Set<BookAuthorLink> links = new LinkedHashSet<>();
        links.add(new BookAuthorLink(1L, "author-key-1"));
        links.add(new BookAuthorLink(2L, "author-key-2"));

        Map<String, Long> authorIdByKey = Map.of("author-key-1", 10L);

        insertBookAuthorLinks(writer, links, authorIdByKey);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Object[]>> paramsCaptor = ArgumentCaptor.forClass(List.class);
        verify(jdbcTemplate, times(1)).batchUpdate(anyString(), paramsCaptor.capture());

        List<Object[]> params = paramsCaptor.getValue();
        assertEquals(1, params.size());
        assertEquals(1L, params.get(0)[0]);
        assertEquals(10L, params.get(0)[1]);
    }

    @Test
    void shouldNotInsertBookAuthorLinksWhenNoAuthorIdMatches() throws Exception {
        @SuppressWarnings("unchecked")
        JdbcBatchItemWriter<GutenbergBook> bookWriter = (JdbcBatchItemWriter<GutenbergBook>) mock(
                JdbcBatchItemWriter.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        CatalogBookWriter writer = new CatalogBookWriter(bookWriter, jdbcTemplate);

        Set<BookAuthorLink> links = Set.of(new BookAuthorLink(1L, "missing-author"));

        insertBookAuthorLinks(writer, links, Map.of());

        verify(jdbcTemplate, never()).batchUpdate(anyString(), any(List.class));
    }

    @Test
    void shouldReturnEmptyArrayWhenAuthorsIsNullOrBlank() throws Exception {
        Object[] parsedFromNull = parseAuthors(null);
        Object[] parsedFromBlank = parseAuthors("   ");

        assertNotNull(parsedFromNull);
        assertNotNull(parsedFromBlank);
        assertEquals(0, parsedFromNull.length);
        assertEquals(0, parsedFromBlank.length);
    }

    @Test
    void shouldParseMultipleAuthorsAndIgnoreBlankTokens() throws Exception {
        Object[] parsed = parseAuthors(" ; Doe, John ; ; Homer 800 BCE- ; ");

        assertEquals(2, parsed.length);

        assertEquals("Doe", invokeAccessor(parsed[0], "lastName"));
        assertEquals("John", invokeAccessor(parsed[0], "firstNames"));
        assertEquals("doe|john||", invokeAccessor(parsed[0], "normalizedKey"));

        assertEquals("Homer", invokeAccessor(parsed[1], "lastName"));
        assertEquals("800 BCE", invokeAccessor(parsed[1], "birthYear"));
        assertEquals("homer||800 bce|", invokeAccessor(parsed[1], "normalizedKey"));
    }

    @Test
    void shouldParseAuthorWithYearsAndNormalizeAccents() throws Exception {
        Object parsed = parseSingleAuthor("García Márquez, Gabriel 1927-2014");

        assertEquals("García Márquez", invokeAccessor(parsed, "lastName"));
        assertEquals("Gabriel", invokeAccessor(parsed, "firstNames"));
        assertEquals("1927", invokeAccessor(parsed, "birthYear"));
        assertEquals("2014", invokeAccessor(parsed, "deathYear"));
        assertEquals("garcia marquez|gabriel|1927|2014", invokeAccessor(parsed, "normalizedKey"));
    }

    @Test
    void shouldParseAuthorWithOpenEndedDeathYear() throws Exception {
        Object parsed = parseSingleAuthor("Homer 800 BCE-");

        assertEquals("Homer", invokeAccessor(parsed, "lastName"));
        assertEquals(null, invokeAccessor(parsed, "firstNames"));
        assertEquals("800 BCE", invokeAccessor(parsed, "birthYear"));
        assertEquals(null, invokeAccessor(parsed, "deathYear"));
        assertEquals("homer||800 bce|", invokeAccessor(parsed, "normalizedKey"));
    }

    @Test
    void shouldParseAuthorWithoutYearsUsingCommaSeparatedNames() throws Exception {
        Object parsed = parseSingleAuthor("Doe, John");

        assertEquals("Doe", invokeAccessor(parsed, "lastName"));
        assertEquals("John", invokeAccessor(parsed, "firstNames"));
        assertEquals(null, invokeAccessor(parsed, "birthYear"));
        assertEquals(null, invokeAccessor(parsed, "deathYear"));
        assertEquals("doe|john||", invokeAccessor(parsed, "normalizedKey"));
    }

    @Test
    void shouldUpsertUniqueAuthorsAndInsertBookAuthorLinks() throws Exception {
        @SuppressWarnings("unchecked")
        JdbcBatchItemWriter<GutenbergBook> bookWriter = (JdbcBatchItemWriter<GutenbergBook>) mock(
                JdbcBatchItemWriter.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), any(), any(), any(), any(), any()))
                .thenReturn(10L, 20L);

        CatalogBookWriter writer = new CatalogBookWriter(bookWriter, jdbcTemplate);

        GutenbergBook first = new GutenbergBook();
        first.setId(1L);
        first.setAuthors("Doe, John 1900-1980;Smith, Jane");

        GutenbergBook second = new GutenbergBook();
        second.setId(2L);
        second.setAuthors("Doe, John 1900-1980");

        writer.write(new Chunk<>(List.of(first, second)));

        verify(bookWriter, times(1)).write(any(Chunk.class));
        verify(jdbcTemplate, times(2)).queryForObject(anyString(), eq(Long.class), any(), any(), any(), any(), any());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Object[]>> paramsCaptor = ArgumentCaptor.forClass(List.class);
        verify(jdbcTemplate, times(1)).batchUpdate(anyString(), paramsCaptor.capture());

        List<Object[]> params = paramsCaptor.getValue();
        assertEquals(3, params.size());
        assertTrue(params.stream().anyMatch(p -> (Long) p[0] == 1L && (Long) p[1] == 10L));
        assertTrue(params.stream().anyMatch(p -> (Long) p[0] == 1L && (Long) p[1] == 20L));
        assertTrue(params.stream().anyMatch(p -> (Long) p[0] == 2L && (Long) p[1] == 10L));
    }

    @Test
    void shouldSkipAuthorLinkInsertionWhenNoAuthorsAreProvided() throws Exception {
        @SuppressWarnings("unchecked")
        JdbcBatchItemWriter<GutenbergBook> bookWriter = (JdbcBatchItemWriter<GutenbergBook>) mock(
                JdbcBatchItemWriter.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        CatalogBookWriter writer = new CatalogBookWriter(bookWriter, jdbcTemplate);

        GutenbergBook book = new GutenbergBook();
        book.setId(99L);
        book.setAuthors("   ");

        writer.write(new Chunk<>(List.of(book)));

        verify(bookWriter, times(1)).write(any(Chunk.class));
        verify(jdbcTemplate, never()).queryForObject(anyString(), eq(Long.class), any(), any(), any(), any(), any());
        verify(jdbcTemplate, never()).batchUpdate(anyString(), any(List.class));
    }

    private static Object parseSingleAuthor(String rawAuthor) throws Exception {
        Method parseSingleAuthorMethod = CatalogBookWriter.class.getDeclaredMethod("parseSingleAuthor", String.class);
        parseSingleAuthorMethod.setAccessible(true);
        return parseSingleAuthorMethod.invoke(null, rawAuthor);
    }

    private static Object[] parseAuthors(String csvAuthors) throws Exception {
        Method parseAuthorsMethod = CatalogBookWriter.class.getDeclaredMethod("parseAuthors", String.class);
        parseAuthorsMethod.setAccessible(true);
        return (Object[]) parseAuthorsMethod.invoke(null, csvAuthors);
    }

    private static void insertBookAuthorLinks(CatalogBookWriter writer, Set<BookAuthorLink> links,
            Map<String, Long> authorIdByKey)
            throws Exception {
        Method method = CatalogBookWriter.class.getDeclaredMethod("insertBookAuthorLinks", Set.class, Map.class);
        method.setAccessible(true);
        method.invoke(writer, links, authorIdByKey);
    }

    private static Object invokeAccessor(Object target, String methodName) throws Exception {
        Method accessor = target.getClass().getDeclaredMethod(methodName);
        accessor.setAccessible(true);
        return accessor.invoke(target);
    }
}
