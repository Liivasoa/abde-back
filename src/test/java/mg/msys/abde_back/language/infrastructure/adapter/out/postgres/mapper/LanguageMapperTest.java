package mg.msys.abde_back.language.infrastructure.adapter.out.postgres.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import mg.msys.abde_back.language.domain.Language;
import mg.msys.abde_back.language.infrastructure.adapter.out.postgres.entity.LanguageEntity;

@DisplayName("LanguageMapper Tests")
class LanguageMapperTest {

    private LanguageMapper languageMapper;

    @BeforeEach
    void setUp() {
        this.languageMapper = Mappers.getMapper(LanguageMapper.class);
    }

    @Test
    @DisplayName("Should convert Language domain to LanguageEntity successfully")
    void testToEntity() {
        // Given
        Language language = new Language("EN", "English");

        // When
        LanguageEntity entity = languageMapper.toEntity(language);

        // Then
        assertAll(
                () -> assertNotNull(entity),
                () -> assertEquals("EN", entity.getCode()),
                () -> assertEquals("English", entity.getLabel()));
    }

    @Test
    @DisplayName("Should convert LanguageEntity to Language domain successfully")
    void testToDomain() {
        // Given
        LanguageEntity entity = new LanguageEntity("FR", "French");

        // When
        Language language = languageMapper.toDomain(entity);

        // Then
        assertAll(
                () -> assertNotNull(language),
                () -> assertEquals("FR", language.getCode()),
                () -> assertEquals("French", language.getLabel()));
    }

    @Test
    @DisplayName("Should maintain code in uppercase during entity conversion")
    void testToEntityPreservesUppercaseCode() {
        // Given
        Language language = new Language("de", "German");

        // When
        LanguageEntity entity = languageMapper.toEntity(language);

        // Then
        assertEquals("DE", entity.getCode());
    }

    @Test
    @DisplayName("Should perform bidirectional mapping correctly")
    void testBidirectionalMapping() {
        // Given
        Language originalLanguage = new Language("ES", "Spanish");

        // When
        LanguageEntity entity = languageMapper.toEntity(originalLanguage);
        Language mappedLanguage = languageMapper.toDomain(entity);

        // Then
        assertAll(
                () -> assertEquals(originalLanguage.getCode(), mappedLanguage.getCode()),
                () -> assertEquals(originalLanguage.getLabel(), mappedLanguage.getLabel()));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when toEntity receives null Language")
    void testToEntityWithNullLanguage() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> languageMapper.toEntity(null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when toDomain receives null LanguageEntity")
    void testToDomainWithNullEntity() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> languageMapper.toDomain(null));
    }

}
