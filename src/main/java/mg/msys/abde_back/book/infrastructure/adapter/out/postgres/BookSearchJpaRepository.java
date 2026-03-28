package mg.msys.abde_back.book.infrastructure.adapter.out.postgres;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model.BookSearchRepositoryCriteria;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model.BookSearchRepositoryResult;
import mg.msys.abde_back.shared.application.port.in.query.dto.PaginatedResult;

@Repository
public class BookSearchJpaRepository {

    private static final String SEARCH_PAGE_SQL = """
            SELECT b.id,
                   b.title,
                   b.issued,
                   b.languages,
                   ARRAY_AGG(a.id ORDER BY a.id) FILTER (WHERE a.id IS NOT NULL) AS author_ids,
                   ARRAY_AGG(BTRIM(CONCAT(COALESCE(a.first_names, ''), ' ', COALESCE(a.last_name, ''))) ORDER BY a.id)
                       FILTER (WHERE a.id IS NOT NULL) AS author_names
              FROM book b
              LEFT JOIN book_author ba ON ba.book_id = b.id
              LEFT JOIN author a ON a.id = ba.author_id
                         WHERE (CAST(:publicationYear AS INTEGER) IS NULL OR EXTRACT(YEAR FROM b.issued) = CAST(:publicationYear AS INTEGER))
                             AND (CAST(:authorName AS TEXT) IS NULL OR EXISTS (
                       SELECT 1
                         FROM book_author ba2
                         JOIN author a2 ON a2.id = ba2.author_id
                        WHERE ba2.book_id = b.id
                          AND BTRIM(CONCAT(COALESCE(a2.first_names, ''), ' ', COALESCE(a2.last_name, ''))) ILIKE CONCAT('%', CAST(:authorName AS TEXT), '%')
                   ))
                             AND (CAST(:language AS TEXT) IS NULL OR b.languages ILIKE CONCAT('%', CAST(:language AS TEXT), '%'))
                             AND (CAST(:title AS TEXT) IS NULL OR b.title ILIKE CONCAT('%', CAST(:title AS TEXT), '%'))
             GROUP BY b.id, b.title, b.issued, b.languages
             ORDER BY b.title
             LIMIT :size OFFSET :offset
            """;

    private static final String COUNT_SQL = """
            SELECT COUNT(DISTINCT b.id)
                FROM book b
             WHERE (CAST(:publicationYear AS INTEGER) IS NULL OR EXTRACT(YEAR FROM b.issued) = CAST(:publicationYear AS INTEGER))
                 AND (CAST(:authorName AS TEXT) IS NULL OR EXISTS (
                                 SELECT 1
                                     FROM book_author ba2
                                     JOIN author a2 ON a2.id = ba2.author_id
                                    WHERE ba2.book_id = b.id
                                        AND BTRIM(CONCAT(COALESCE(a2.first_names, ''), ' ', COALESCE(a2.last_name, ''))) ILIKE CONCAT('%', CAST(:authorName AS TEXT), '%')
                         ))
                 AND (CAST(:language AS TEXT) IS NULL OR b.languages ILIKE CONCAT('%', CAST(:language AS TEXT), '%'))
                 AND (CAST(:title AS TEXT) IS NULL OR b.title ILIKE CONCAT('%', CAST(:title AS TEXT), '%'))
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BookSearchJpaRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PaginatedResult<BookSearchRepositoryResult> search(BookSearchRepositoryCriteria criteria) {
        Map<String, Object> params = toFilterParams(criteria);
        params.put("size", criteria.size());
        params.put("offset", criteria.offset());

        List<BookSearchRepositoryResult> items = jdbcTemplate.query(SEARCH_PAGE_SQL, params, bookRowMapper());
        long total = jdbcTemplate.queryForObject(COUNT_SQL, params, Long.class);

        return PaginatedResult.of(items, criteria.page(), criteria.size(), total);
    }

    private Map<String, Object> toFilterParams(BookSearchRepositoryCriteria criteria) {
        Map<String, Object> params = new HashMap<>();
        params.put("publicationYear", criteria.publicationYear());
        params.put("authorName", criteria.authorName());
        params.put("language", criteria.language());
        params.put("title", criteria.title());
        return params;
    }

    private RowMapper<BookSearchRepositoryResult> bookRowMapper() {
        return (rs, rowNum) -> {
            LocalDate issued = rs.getDate("issued") == null ? null : rs.getDate("issued").toLocalDate();
            return new BookSearchRepositoryResult(
                    rs.getLong("id"),
                    rs.getString("title"),
                    issued,
                    rs.getString("languages"),
                    toAuthorReferences(rs.getArray("author_ids"), rs.getArray("author_names")));
        };
    }

    private List<BookSearchRepositoryResult.AuthorReference> toAuthorReferences(Array authorIdsArray,
            Array authorNamesArray)
            throws SQLException {
        if (authorIdsArray == null || authorNamesArray == null) {
            return List.of();
        }

        Object[] idValues = (Object[]) authorIdsArray.getArray();
        Object[] nameValues = (Object[]) authorNamesArray.getArray();
        if (idValues == null || nameValues == null || idValues.length == 0 || nameValues.length == 0) {
            return List.of();
        }

        int pairCount = Math.min(idValues.length, nameValues.length);
        List<BookSearchRepositoryResult.AuthorReference> authors = new ArrayList<>(pairCount);
        for (int i = 0; i < pairCount; i++) {
            Long authorId = toLong(idValues[i]);
            String fullName = nameValues[i] == null ? null : nameValues[i].toString().trim();
            if (authorId != null && fullName != null && !fullName.isEmpty()) {
                authors.add(new BookSearchRepositoryResult.AuthorReference(authorId, fullName));
            }
        }
        return authors;
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }
}
