package mg.msys.abde_back.application.fake;

import java.util.ArrayList;
import java.util.List;

import mg.msys.abde_back.application.port.LanguagePersistencePort;
import mg.msys.abde_back.domain.model.Language;

public class LanguagePersistencePortFake implements LanguagePersistencePort {

    private final List<Language> savedLanguages = new ArrayList<>();
    private int saveCallCount = 0;
    private boolean shouldThrowException = false;

    @Override
    public void save(Language language) {
        if (shouldThrowException) {
            throw new IllegalArgumentException("Language already exists");
        }
        saveCallCount++;
        savedLanguages.add(language);
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
}
