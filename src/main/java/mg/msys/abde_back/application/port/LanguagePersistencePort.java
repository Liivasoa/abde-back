package mg.msys.abde_back.application.port;

import java.util.Optional;

import mg.msys.abde_back.domain.model.Language;

/**
 * Output port for persisting languages.
 * This port defines the contract for saving languages to any data store.
 */
public interface LanguagePersistencePort {

    /**
     * Saves a language.
     *
     * @param language the language to save
     * @return the saved language
     * @throws IllegalArgumentException if language violates business rules
     */
    void save(Language language);

    Optional<Language> findByCode(String code);
}
