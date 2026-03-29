package mg.msys.abde_back.language.application.port.in.query.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("[Domain/Model] Language Book Count Result Tests")
class LanguageBookCountResultTest {

    @Test
    @DisplayName("Should normalize code to uppercase and trim values")
    void shouldNormalizeCodeAndLabel() {
        LanguageBookCountResult result = new LanguageBookCountResult(" fr ", " Francais ", 3);

        assertEquals("FR", result.code());
        assertEquals("Francais", result.label());
        assertEquals(3, result.bookCount());
    }

    @Test
    @DisplayName("Should fallback label to code when label is blank")
    void shouldFallbackLabelToCode() {
        LanguageBookCountResult result = new LanguageBookCountResult("en", "   ", 1);

        assertEquals("EN", result.code());
        assertEquals("EN", result.label());
    }

    @Test
    @DisplayName("Should reject blank code")
    void shouldRejectBlankCode() {
        assertThrows(IllegalArgumentException.class, () -> new LanguageBookCountResult(" ", "English", 1));
    }

    @Test
    @DisplayName("Should reject negative book count")
    void shouldRejectNegativeBookCount() {
        assertThrows(IllegalArgumentException.class, () -> new LanguageBookCountResult("EN", "English", -1));
    }
}
