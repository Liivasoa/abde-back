package mg.msys.abde_back.adapter.out.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;

import mg.msys.abde_back.adapter.out.entity.LanguageEntity;
import mg.msys.abde_back.domain.model.Language;

/**
 * Mapper interface for converting between Language domain model and LanguageEntity.
 * Generated implementation will be excluded from Jacoco coverage.
 */
@Mapper(componentModel = "spring")
public interface LanguageMapper {

    LanguageEntity toEntity(Language language);

    Language toDomain(LanguageEntity languageEntity);

    @BeforeMapping
    default void validateLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
    }

    @BeforeMapping
    default void validateLanguage(LanguageEntity language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
    }

}
