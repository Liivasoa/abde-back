package mg.msys.abde_back.application.service.language;

import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.application.port.LanguagePersistencePort;
import mg.msys.abde_back.application.usecase.language.AddLanguageUseCase;
import mg.msys.abde_back.domain.model.Language;

@RequiredArgsConstructor
public class AddLanguageService implements AddLanguageUseCase {

    private final LanguagePersistencePort languagePersistencePort;

    public Language execute(String code, String label) {
        // Create the language domain entity
        Language language = new Language(code, label);

        // Persist the language using the output port
        return languagePersistencePort.save(language);
    }
}
