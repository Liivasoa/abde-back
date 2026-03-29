package mg.msys.abde_back.language.infrastructure.adapter.out.postgres.mapper;

import org.mapstruct.Mapper;

import mg.msys.abde_back.language.application.port.in.query.dto.LanguageBookCountResult;
import mg.msys.abde_back.language.infrastructure.adapter.out.postgres.model.LanguageAvailableResult;

@Mapper(componentModel = "spring")
public interface LanguageAvailableResultMapper {

    LanguageBookCountResult toDto(LanguageAvailableResult result);
}
