package mg.msys.abde_back.batch.task;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import mg.msys.abde_back.batch.entity.Author;
import mg.msys.abde_back.batch.entity.AuthorAggregation;
import mg.msys.abde_back.batch.entity.BookAuthorLink;
import mg.msys.abde_back.batch.entity.GutenbergBook;

public final class CatalogBookWriter implements ItemWriter<GutenbergBook> {
    private static final String UPSERT_AUTHOR_SQL = """
            INSERT INTO author (last_name, first_names, birth_year, death_year, normalized_key)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (normalized_key)
            DO UPDATE SET normalized_key = EXCLUDED.normalized_key
            RETURNING id
            """;

    private static final String INSERT_BOOK_AUTHOR_SQL = """
            INSERT INTO book_author (book_id, author_id)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
            """;

    private final JdbcBatchItemWriter<GutenbergBook> bookWriter;
    private final JdbcTemplate jdbcTemplate;

    public CatalogBookWriter(JdbcBatchItemWriter<GutenbergBook> bookWriter, JdbcTemplate jdbcTemplate) {
        this.bookWriter = bookWriter;
        this.jdbcTemplate = jdbcTemplate;
    }

    public static CatalogBookWriter create(DataSource dataSource) {
        JdbcBatchItemWriter<GutenbergBook> bookWriter = new JdbcBatchItemWriterBuilder<GutenbergBook>()
                .dataSource(dataSource)
                .sql("INSERT INTO book (id, title, issued, languages, subjects) VALUES (:id, :title, :issued, :languages, :subjects) ON CONFLICT (id) DO NOTHING")
                .beanMapped()
                .assertUpdates(false)
                .build();
        bookWriter.afterPropertiesSet();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return new CatalogBookWriter(bookWriter, jdbcTemplate);
    }

    @Override
    public void write(Chunk<? extends GutenbergBook> chunk) throws Exception {
        bookWriter.write(chunk);

        AuthorAggregation aggregation = collectAuthorsAndLinks(chunk);
        Map<String, Long> authorIdByKey = upsertAuthors(aggregation.uniqueAuthors());
        insertBookAuthorLinks(aggregation.links(), authorIdByKey);
    }

    private AuthorAggregation collectAuthorsAndLinks(Chunk<? extends GutenbergBook> chunk) {
        Map<String, Author> uniqueAuthors = new LinkedHashMap<>();
        Set<BookAuthorLink> links = new LinkedHashSet<>();

        for (GutenbergBook book : chunk.getItems()) {
            for (Author authorRecord : parseAuthors(book.getAuthors())) {
                uniqueAuthors.putIfAbsent(authorRecord.normalizedKey(), authorRecord);
                links.add(new BookAuthorLink(book.getId(), authorRecord.normalizedKey()));
            }
        }

        return new AuthorAggregation(uniqueAuthors, links);
    }

    private Map<String, Long> upsertAuthors(Map<String, Author> uniqueAuthors) {
        Map<String, Long> authorIdByKey = new HashMap<>();

        for (Author authorRecord : uniqueAuthors.values()) {
            Long authorId = jdbcTemplate.queryForObject(
                    UPSERT_AUTHOR_SQL,
                    Long.class,
                    authorRecord.lastName(),
                    authorRecord.firstNames(),
                    authorRecord.birthYear(),
                    authorRecord.deathYear(),
                    authorRecord.normalizedKey());
            if (authorId != null) {
                authorIdByKey.put(authorRecord.normalizedKey(), authorId);
            }
        }

        return authorIdByKey;
    }

    private void insertBookAuthorLinks(Set<BookAuthorLink> links, Map<String, Long> authorIdByKey) {
        List<Object[]> linkParams = new ArrayList<>();

        for (BookAuthorLink link : links) {
            Long authorId = authorIdByKey.get(link.authorKey());
            if (authorId != null) {
                linkParams.add(new Object[] { link.bookId(), authorId });
            }
        }

        if (!linkParams.isEmpty()) {
            jdbcTemplate.batchUpdate(INSERT_BOOK_AUTHOR_SQL, linkParams);
        }
    }

    private static Author[] parseAuthors(String csvAuthors) {
        if (csvAuthors == null || csvAuthors.isBlank()) {
            return new Author[0];
        }

        return Arrays.stream(csvAuthors.split(";"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(CatalogBookWriter::parseSingleAuthor)
                .toArray(Author[]::new);
    }

    private static Author parseSingleAuthor(String rawAuthor) {
        String value = rawAuthor.trim();
        String birthYear = null;
        String deathYear = null;

        java.util.regex.Matcher yearsMatcher = java.util.regex.Pattern
                .compile("^(.*?)(?:,\\s*)?([0-9]{3,4}\\??(?:\\s*BCE)?)\\s*-\\s*([0-9]{0,4}\\??(?:\\s*BCE)?)\\s*$",
                        java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(value);

        if (yearsMatcher.matches()) {
            value = yearsMatcher.group(1).trim();
            birthYear = yearsMatcher.group(2).trim();
            String parsedDeathYear = yearsMatcher.group(3).trim();
            deathYear = parsedDeathYear.isBlank() ? null : parsedDeathYear;
        }

        String lastName = value;
        String firstNames = null;
        int firstComma = value.indexOf(',');
        if (firstComma >= 0) {
            lastName = value.substring(0, firstComma).trim();
            firstNames = value.substring(firstComma + 1).trim();
            if (firstNames.isBlank()) {
                firstNames = null;
            }
        }

        String normalizedKey = normalize(lastName) + "|" + normalize(firstNames) + "|" + normalize(birthYear) + "|"
                + normalize(deathYear);

        return new Author(lastName, firstNames, birthYear, deathYear, normalizedKey);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

}
