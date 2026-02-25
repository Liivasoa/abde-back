package mg.msys.abde_back.application.usecase.language;

import mg.msys.abde_back.domain.model.Language;

public interface AddLanguageUseCase {

    /**
     * Executes the add language use case.
     *
     * @param code  the language code (will be converted to uppercase)
     * @param label the language label
     * @return the created language
     * @throws IllegalArgumentException if code or label is invalid
     */
    public Language execute(String code, String label);
}
