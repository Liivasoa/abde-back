package mg.msys.abde_back.adapter.out.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;

import mg.msys.abde_back.adapter.out.entity.LanguageEntity;
import mg.msys.abde_back.language.domain.Language;

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
