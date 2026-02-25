package mg.msys.abde_back.domain.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Domain entity representing a Language.
 * Code must be unique and is automatically converted to uppercase.
 * Label is the human-readable name of the language.
 * 
 * This entity enforces business invariants:
 * - Code: unique, non-null, non-empty, automatically uppercased
 * - Label: non-null, non-empty, mutable
 */
@Getter
@ToString
@EqualsAndHashCode
public class Language {
    
    @Getter(AccessLevel.PUBLIC)
    private final String code;
    
    private String label;
    
    /**
     * Creates a new Language.
     *
     * @param code the unique language code (will be converted to uppercase)
     * @param label the human-readable name
     * @throws IllegalArgumentException if code or label is null or empty
     */
    public Language(String code, String label) {
        this.code = validateAndNormalizeCode(code);
        this.label = validateLabel(label);
    }
    
    /**
     * Updates the language label.
     *
     * @param label the new label
     * @throws IllegalArgumentException if label is null or empty
     */
    public void setLabel(String label) {
        this.label = validateLabel(label);
    }
    
    /**
     * Validates and normalizes the code to uppercase.
     *
     * @param code the code to validate
     * @return the normalized code (uppercased)
     * @throws IllegalArgumentException if code is null or empty
     */
    private String validateAndNormalizeCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Language code cannot be null or empty");
        }
        return code.toUpperCase();
    }
    
    /**
     * Validates the label using modern Java features.
     *
     * @param label the label to validate
     * @return the validated label
     * @throws IllegalArgumentException if label is null or empty
     */
    private String validateLabel(String label) {
        return switch (label) {
            case null -> throw new IllegalArgumentException("Language label cannot be null or empty");
            case String s -> {
                if (s.trim().isEmpty()) {
                    throw new IllegalArgumentException("Language label cannot be null or empty");
                }
                yield s;
            }
        };
    }
}
