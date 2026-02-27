package mg.msys.abde_back.application.usecase.language;

public interface AddLanguageUseCase {

    /**
     * Executes the add language use case.
     *
     * @param code  the language code (will be converted to uppercase)
     * @param label the language label
     * @return the created language
     * @throws IllegalArgumentException if code or label is invalid
     */
    public void execute(String code, String label);
}
