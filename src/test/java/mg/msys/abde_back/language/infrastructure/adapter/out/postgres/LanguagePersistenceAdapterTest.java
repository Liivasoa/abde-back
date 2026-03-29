package mg.msys.abde_back.language.infrastructure.adapter.out.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import mg.msys.abde_back.language.application.port.in.query.dto.LanguageBookCountResult;
import mg.msys.abde_back.language.application.port.out.LanguagePersistencePort;
import mg.msys.abde_back.language.domain.Language;
import mg.msys.abde_back.language.infrastructure.adapter.out.postgres.mapper.LanguageMapper;
import mg.msys.abde_back.shared.infrastructure.adapter.out.postgres.AbstractAdapterTest;

public class LanguagePersistenceAdapterTest extends AbstractAdapterTest {

    @Autowired
    private LanguageJpaRepository languageJpaRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LanguageMapper languageMapper;
    private LanguagePersistencePort languagePersistencePort;

    @BeforeEach
    void setUp() {
        this.languageMapper = Mappers.getMapper(LanguageMapper.class);
        this.languagePersistencePort = new LanguagePersistenceAdapter(
                languageJpaRepository,
                languageMapper,
                new LanguageAvailableJdbcRepository(namedParameterJdbcTemplate));
    }

    @Test
    @DisplayName("Should save language and retrieve it successfully")
    void testSaveLanguageSuccessfully() {
        Language language = new Language("EN", "English");
        languagePersistencePort.save(language);

        languagePersistencePort.findByCode("EN").ifPresent(savedLanguage -> {
            assert savedLanguage.getLabel().equals("English");
            assertAll(
                    () -> assertEquals("EN", savedLanguage.getCode()),
                    () -> assertEquals("English", savedLanguage.getLabel()));
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when saving language with null code")
    void testSaveLanguageWithNullCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Language(null, "English");
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when saving language with empty code")
    void testSaveLanguageWithEmptyCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Language("", "English");
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when saving language with null label")
    void testSaveLanguageWithNullLabel() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Language("EN", null);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when saving language with empty label")
    void testSaveLanguageWithEmptyLabel() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Language("EN", "");
        });
    }

    @Test
    @DisplayName("Should save language with lowercase code and convert it to uppercase")
    void testSaveLanguageWithLowercaseCode() {
        Language language = new Language("en", "English");
        languagePersistencePort.save(language);

        languagePersistencePort.findByCode("EN").ifPresent(savedLanguage -> {
            assertEquals("EN", savedLanguage.getCode());
            assertEquals("English", savedLanguage.getLabel());
        });
    }

    @Test
    @DisplayName("Should update language when saved again with same code")
    void testUpdateLanguageLabel() {
        Language language = new Language("EN", "English");
        Language savedLanguage = languagePersistencePort.save(language);

        // Update the label and save again
        savedLanguage.setLabel("English (Updated)");
        Language updatedLanguage = languagePersistencePort.save(savedLanguage);

        languagePersistencePort.findByCode("EN").ifPresent(retrieved -> {
            assertEquals("English (Updated)", retrieved.getLabel());
        });
    }

    @Test
    @DisplayName("Should save and retrieve multiple languages")
    void testSaveMultipleLanguages() {
        Language en = new Language("EN", "English");
        Language fr = new Language("FR", "French");
        Language es = new Language("ES", "Spanish");

        languagePersistencePort.save(en);
        languagePersistencePort.save(fr);
        languagePersistencePort.save(es);

        assertTrue(languagePersistencePort.findByCode("EN").isPresent());
        assertTrue(languagePersistencePort.findByCode("FR").isPresent());
        assertTrue(languagePersistencePort.findByCode("ES").isPresent());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when code is only whitespace")
    void testSaveLanguageWithWhitespaceOnlyCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Language("   ", "English");
        });
    }

    @Test
    @DisplayName("Should return available languages with book count from book.languages column")
    void testFindAvailableLanguagesWithBookCount() {
        jdbcTemplate.update("INSERT INTO book(id, issued, title, languages, subjects) VALUES (100, ?, ?, ?, ?)",
                LocalDate.of(2020, 1, 1), "Book EN 1", "EN", "subject");
        jdbcTemplate.update("INSERT INTO book(id, issued, title, languages, subjects) VALUES (101, ?, ?, ?, ?)",
                LocalDate.of(2021, 1, 1), "Book EN 2", "EN", "subject");
        jdbcTemplate.update("INSERT INTO book(id, issued, title, languages, subjects) VALUES (102, ?, ?, ?, ?)",
                LocalDate.of(2022, 1, 1), "Book FR", "FR", "subject");
        jdbcTemplate.update("INSERT INTO book(id, issued, title, languages, subjects) VALUES (103, ?, ?, ?, ?)",
                LocalDate.of(2023, 1, 1), "Book EN+FR", "EN,FR", "subject");

        jdbcTemplate.update("INSERT INTO language(code, label) VALUES (?, ?)", "EN", "English");

        List<LanguageBookCountResult> result = languagePersistencePort.findAvailableLanguagesWithBookCount();

        assertThat(result).isNotEmpty();
        LanguageBookCountResult en = result.stream().filter(r -> "EN".equals(r.code())).findFirst().orElseThrow();
        LanguageBookCountResult fr = result.stream().filter(r -> "FR".equals(r.code())).findFirst().orElseThrow();

        assertEquals("EN", en.code());
        assertEquals("English", en.label());
        assertEquals(3, en.bookCount());

        assertEquals("FR", fr.code());
        assertEquals("FR", fr.label());
        assertEquals(2, fr.bookCount());
    }

}
