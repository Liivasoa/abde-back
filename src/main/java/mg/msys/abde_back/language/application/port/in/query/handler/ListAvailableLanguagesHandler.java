package mg.msys.abde_back.language.application.port.in.query.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.language.application.port.in.query.ListAvailableLanguagesUseCase;
import mg.msys.abde_back.language.application.port.in.query.dto.LanguageBookCountResult;
import mg.msys.abde_back.language.application.port.out.LanguagePersistencePort;

@Component
@RequiredArgsConstructor
public class ListAvailableLanguagesHandler implements ListAvailableLanguagesUseCase {

    private final LanguagePersistencePort languagePersistencePort;

    @Override
    public List<LanguageBookCountResult> execute() {
        return languagePersistencePort.findAvailableLanguagesWithBookCount();
    }
}
