package mg.msys.abde_back.adapter.out;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import mg.msys.abde_back.adapter.out.mapper.LanguageMapper;
import mg.msys.abde_back.application.port.LanguagePersistencePort;
import mg.msys.abde_back.domain.model.Language;
import mg.msys.abde_back.infrastructure.repository.LanguageJpaRepository;

public class LanguagePersistenceAdapterTest extends AbstractAdapterTest {

    @Autowired
    private LanguageJpaRepository languageJpaRepository;

    private LanguageMapper languageMapper;
    private LanguagePersistencePort languagePersistencePort;

    @BeforeEach
    void setUp() {
        this.languageMapper = Mappers.getMapper(LanguageMapper.class);
        this.languagePersistencePort = new LanguagePersistenceAdapter(languageJpaRepository, languageMapper);
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
        languagePersistencePort.save(language);

        language.setLabel("English (Updated)");
        languagePersistencePort.save(language);

        languagePersistencePort.findByCode("EN").ifPresent(savedLanguage -> {
            assertEquals("English (Updated)", savedLanguage.getLabel());
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

}
