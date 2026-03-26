package mg.msys.abde_back.language.application.port.in.command.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.language.application.port.in.command.CreateLanguageUseCase;
import mg.msys.abde_back.language.application.port.out.LanguagePersistencePort;
import mg.msys.abde_back.language.domain.Language;

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
