package mg.msys.abde_back.language.infrastructure.adapter.out.postgres.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;

import mg.msys.abde_back.language.domain.Language;
import mg.msys.abde_back.language.infrastructure.adapter.out.postgres.entity.LanguageEntity;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    LanguageEntity toEntity(Language language);

    Language toDomain(LanguageEntity languageEntity);

    @BeforeMapping
    default void validateDomainLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
    }

    @BeforeMapping
    default void validateEntityLanguage(LanguageEntity language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
    }

}
