package mg.msys.abde_back.language.application.port.in.query;

import java.util.List;

import mg.msys.abde_back.language.application.port.in.query.dto.LanguageBookCountResult;

public interface ListAvailableLanguagesUseCase {

    List<LanguageBookCountResult> execute();
}
