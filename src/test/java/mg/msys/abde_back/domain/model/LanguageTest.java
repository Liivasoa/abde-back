package mg.msys.abde_back.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("[Domain/Model] Language Entity Tests")
public class LanguageTest {

    @Nested
    class TestLanguageCreation {

        @Test
        void shouldCreateLanguageWithValidData() {
            // Given
            String code = "FR";
            String label = "French";

            // When
            Language Language = new Language(code, label);

            // Then
            assertEquals(code, Language.getCode());
            assertEquals(label, Language.getLabel());
        }

        @Test
        void shouldNotCreateLanguageWithNullCode() {
            // Given
            String label = "French";

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> {
                Language Language = new Language(null, label);
            });
        }

        @Test
        void shouldNotCreateLanguageWithEmptyCode() {
            // Given
            String code = "";
            String label = "French";

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> {
                Language Language = new Language(code, label);
            });
        }

        @Test
        void shouldConvertCodeToUpperCase() {
            // Given
            String code = "fr";
            String label = "French";

            // When
            Language Language = new Language(code, label);

            // Then
            assertEquals("FR", Language.getCode());
        }

        @Test
        void shouldNotCreateLanguageWithNullLabel() {
            // Given
            String code = "FR";

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> {
                Language Language = new Language(code, null);
            });
        }

        @Test
        void shouldNotCreateLanguageWithEmptyLabel() {
            // Given
            String code = "FR";
            String label = "";

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> {
                Language Language = new Language(code, label);
            });
        }
    }

    @Nested
    class TestLanguageModification {

        @Test
        void shouldUpdateLanguageLabel() {
            // Given
            Language Language = new Language("FR", "French");
            String newLabel = "Français";

            // When
            Language.setLabel(newLabel);

            // Then
            assertEquals(newLabel, Language.getLabel());
        }

        @Test
        void shouldNotSetNullLabel() {
            // Given
            Language Language = new Language("FR", "French");

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> {
                Language.setLabel(null);
            });
        }

        @Test
        void shouldNotSetEmptyLabel() {
            // Given
            Language Language = new Language("FR", "French");

            // When / Then
            assertThrows(IllegalArgumentException.class, () -> {
                Language.setLabel("");
            });
        }
    }
}
