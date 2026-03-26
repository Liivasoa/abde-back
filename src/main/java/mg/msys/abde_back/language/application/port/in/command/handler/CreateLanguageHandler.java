package mg.msys.abde_back.language.application.port.in.command.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.application.port.LanguagePersistencePort;
import mg.msys.abde_back.domain.model.Language;
import mg.msys.abde_back.language.application.port.in.command.CreateLanguageUseCase;

@Component
@RequiredArgsConstructor
public class CreateLanguageHandler implements CreateLanguageUseCase {

    private final LanguagePersistencePort languagePersistencePort;

    @Override
    public Language execute(String code, String label) {
        // Create the language domain entity
        Language language = new Language(code, label);

        // Persist the language using the output port and return the saved entity
        return languagePersistencePort.save(language);
    }
}
