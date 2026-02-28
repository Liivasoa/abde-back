package mg.msys.abde_back.adapter.out.mapper;

import org.mapstruct.Mapper;

import mg.msys.abde_back.adapter.out.entity.LanguageEntity;
import mg.msys.abde_back.domain.model.Language;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    LanguageEntity toEntity(Language language);

    Language toDomain(LanguageEntity languageEntity);

}
