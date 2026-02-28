package mg.msys.abde_back.adapter.out;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import mg.msys.abde_back.adapter.out.mapper.LanguageMapper;
import mg.msys.abde_back.adapter.out.repository.LanguageJpaRepository;
import mg.msys.abde_back.application.port.LanguagePersistencePort;
import mg.msys.abde_back.domain.model.Language;

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
        // Given
        Language language = new Language("EN", "English");

        // When
        languagePersistencePort.save(language);

        // Then
        languagePersistencePort.findByCode("EN").ifPresent(savedLanguage -> {
            assert savedLanguage.getLabel().equals("English");
            assertAll(
                    () -> assertEquals("EN", savedLanguage.getCode()),
                    () -> assertEquals("English", savedLanguage.getLabel()));
        });
    }
}
