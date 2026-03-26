package mg.msys.abde_back.language.application.port.in.command;

import mg.msys.abde_back.domain.model.Language;

public interface CreateLanguageUseCase {

    /**
     * Executes the add language use case.
     *
     * @param code  the language code (will be converted to uppercase)
     * @param label the language label
     * @return the created language
     * @throws IllegalArgumentException if code or label is invalid
     */
    Language execute(String code, String label);
}
