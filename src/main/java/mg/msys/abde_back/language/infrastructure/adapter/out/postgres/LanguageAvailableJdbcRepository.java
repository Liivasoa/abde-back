package mg.msys.abde_back.language.infrastructure.adapter.out.postgres;

import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import mg.msys.abde_back.language.application.port.in.query.dto.LanguageBookCountResult;

@Repository
public class LanguageAvailableJdbcRepository {

    private static final String AVAILABLE_LANGUAGES_SQL = """
            SELECT UPPER(BTRIM(lang_code))                              AS code,
                   COALESCE(l.label, UPPER(BTRIM(lang_code)))           AS label,
                   COUNT(DISTINCT b.id)                                  AS book_count
              FROM book b,
                   regexp_split_to_table(b.languages, ';') AS lang_code
              LEFT JOIN language l ON l.code = UPPER(BTRIM(lang_code))
             GROUP BY UPPER(BTRIM(lang_code)), l.label
               ORDER BY book_count DESC, code ASC
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LanguageAvailableJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LanguageBookCountResult> findAvailableLanguagesWithBookCount() {
        return jdbcTemplate.query(AVAILABLE_LANGUAGES_SQL, (rs, rowNum) -> new LanguageBookCountResult(
                rs.getString("code"),
                rs.getString("label"),
                rs.getLong("book_count")));
    }
}
