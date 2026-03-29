package mg.msys.abde_back.language.application.port.out;

import java.util.ArrayList;
import java.util.List;

import mg.msys.abde_back.language.application.port.in.query.dto.LanguageBookCountResult;
import mg.msys.abde_back.language.domain.Language;

public class LanguagePersistencePortFake implements LanguagePersistencePort {

    private final List<Language> savedLanguages = new ArrayList<>();
    private int saveCallCount = 0;
    private boolean shouldThrowException = false;
    private List<LanguageBookCountResult> availableLanguagesToReturn = new ArrayList<>();
    private int availableLanguagesCallCount;

    @Override
    public Language save(Language language) {
        if (shouldThrowException) {
            throw new IllegalArgumentException("Language already exists");
        }
        saveCallCount++;
        Language savedLanguage = new Language(language.getCode(), language.getLabel());
        savedLanguages.add(savedLanguage);
        return savedLanguage;
    }

    @Override
    public java.util.Optional<Language> findByCode(String code) {
        return savedLanguages.stream()
                .filter(lang -> lang.getCode().equals(code))
                .findFirst();
    }

    public List<Language> getSavedLanguages() {
        return new ArrayList<>(savedLanguages);
    }

    public int getSaveCallCount() {
        return saveCallCount;
    }

    public void setShouldThrowException(boolean shouldThrow) {
        this.shouldThrowException = shouldThrow;
    }

    @Override
    public List<LanguageBookCountResult> findAvailableLanguagesWithBookCount() {
        availableLanguagesCallCount++;
        return List.copyOf(availableLanguagesToReturn);
    }

    public void setAvailableLanguagesToReturn(List<LanguageBookCountResult> languages) {
        this.availableLanguagesToReturn = new ArrayList<>(languages);
    }

    public int getAvailableLanguagesCallCount() {
        return availableLanguagesCallCount;
    }
}
