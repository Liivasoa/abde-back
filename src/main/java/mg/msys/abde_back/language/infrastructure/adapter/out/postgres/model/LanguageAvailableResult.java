package mg.msys.abde_back.language.infrastructure.adapter.out.postgres.model;

/**
 * Internal persistence model for available language results.
 * Decouples the persistence layer from the application query DTOs.
 */
public record LanguageAvailableResult(
        String code,
        String label,
        long bookCount) {
}
