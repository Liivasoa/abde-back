package mg.msys.abde_back.language.application.port.in.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import mg.msys.abde_back.language.application.port.in.command.handler.CreateLanguageHandler;
import mg.msys.abde_back.language.application.port.out.LanguagePersistencePortFake;
import mg.msys.abde_back.language.domain.Language;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for AddLanguageUseCase.
 * Tests use case orchestration and port interactions.
 * Uses Fake implementation to simulate repository behavior.
 * 
 * Strategy (Following TestEngineer rules):
 * - Focus on use case logic, not domain validation (tested in LanguageTest)
 * - Use Fake instead of Mockito for output port
 * - Test main flow and alternative flows
 * - Avoid testing framework code or external libraries
 */
@DisplayName("[Application/Use case] Add Language Use Case Tests")
class CreateLanguageUseCaseTest {

    private CreateLanguageUseCase useCase;
    private LanguagePersistencePortFake fakePort;

    @BeforeEach
    void setUp() {
        fakePort = new LanguagePersistencePortFake();
        useCase = new CreateLanguageHandler(fakePort);
    }

    // ==================== MAIN FLOW TESTS ====================

    @Nested
    @DisplayName("Main Flow - Add Language")
    class MainFlowTests {

        @Test
        @DisplayName("Should save language through port and return result")
        void testAddLanguageSuccessfully() {
            // Given
            String code = "FR";
            String label = "French";

            // When
            useCase.execute(code, label);

            // Then
            Language result = fakePort.getSavedLanguages().get(0);
            assertNotNull(result);
            assertEquals("FR", result.getCode());
            assertEquals("French", result.getLabel());
            assertEquals(1, fakePort.getSavedLanguages().size());
        }

        @Test
        @DisplayName("Should persist language in port")
        void testLanguagePersisted() {
            // Given
            String code = "EN";
            String label = "English";

            // When
            useCase.execute(code, label);

            // Then
            List<Language> saved = fakePort.getSavedLanguages();
            assertEquals(1, saved.size());
            assertEquals("EN", saved.get(0).getCode());
            assertEquals("English", saved.get(0).getLabel());
        }

        @Test
        @DisplayName("Should add multiple languages in sequence")
        void testAddMultipleLanguages() {
            // Given

            // When
            useCase.execute("FR", "French");
            useCase.execute("EN", "English");
            useCase.execute("ES", "Spanish");

            // Then
            assertEquals(3, fakePort.getSavedLanguages().size());
        }
    }

    // ==================== ALTERNATIVE FLOWS - DOMAIN VALIDATION
    // ====================

    @Nested
    @DisplayName("Alternative Flows - Invalid Language Data")
    class AlternativeFlowsTests {

        @Test
        @DisplayName("Should not persist when code is null (domain validation)")
        void testRejectNullCode() {
            // When / Then
            assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(null, "French"));
            assertEquals(0, fakePort.getSavedLanguages().size());
        }

        @Test
        @DisplayName("Should not persist when label is null (domain validation)")
        void testRejectNullLabel() {
            // When / Then
            assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute("FR", null));
            assertEquals(0, fakePort.getSavedLanguages().size());
        }

        @Test
        @DisplayName("Should not persist when code is empty (domain validation)")
        void testRejectEmptyCode() {
            // When / Then
            assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute("", "French"));
            assertEquals(0, fakePort.getSavedLanguages().size());
        }

        @Test
        @DisplayName("Should not persist when label is empty (domain validation)")
        void testRejectEmptyLabel() {
            // When / Then
            assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute("FR", ""));
            assertEquals(0, fakePort.getSavedLanguages().size());
        }
    }

    // ==================== PORT INTERACTION TESTS ====================

    @Nested
    @DisplayName("Port Interactions")
    class PortInteractionTests {

        @Test
        @DisplayName("Should call port save method")
        void testPortSaveIsCalled() {
            // When
            useCase.execute("DE", "German");

            // Then
            assertEquals(1, fakePort.getSaveCallCount());
        }

        @Test
        @DisplayName("Should return port result as-is")
        void testReturnPortResult() {
            // Given

            // When
            useCase.execute("IT", "Italian");

            // Then
            List<Language> savedLanguages = fakePort.getSavedLanguages();
            assertNotNull(savedLanguages);
            assertEquals(1, savedLanguages.size());
            assertEquals("IT", savedLanguages.get(0).getCode());
        }

        @Test
        @DisplayName("Should handle port throwing IllegalArgumentException")
        void testHandlePortIllegalArgumentException() {
            // Given
            fakePort.setShouldThrowException(true);

            // When / Then
            assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute("FR", "French"));
        }
    }

    // ==================== EDGE CASES ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle lowercase code conversion (domain model responsibility)")
        void testLowercaseCodeHandledByDomain() {
            // When
            useCase.execute("fr", "French");

            // Then
            List<Language> savedLanguages = fakePort.getSavedLanguages();
            assertEquals(1, savedLanguages.size());
            assertEquals("FR", savedLanguages.get(0).getCode());
        }

        @Test
        @DisplayName("Should preserve label exactly as provided")
        void testLabelPreservedExactly() {
            // When
            useCase.execute("FR", "Français (France)");

            // Then
            List<Language> savedLanguages = fakePort.getSavedLanguages();
            assertEquals(1, savedLanguages.size());
            assertEquals("Français (France)", savedLanguages.get(0).getLabel());
        }

        @Test
        @DisplayName("Should work with single character code")
        void testSingleCharacterCode() {
            // When
            useCase.execute("Z", "Zealandian");

            // Then
            List<Language> savedLanguages = fakePort.getSavedLanguages();
            assertEquals(1, savedLanguages.size());
            assertEquals("Z", savedLanguages.get(0).getCode());
        }
    }
}
