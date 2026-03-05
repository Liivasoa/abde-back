package mg.msys.abde_back.adapter.in;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import mg.msys.abde_back.application.usecase.language.AddLanguageUseCase;
import mg.msys.abde_back.domain.model.Language;

@DisplayName("[Adapter/In] Add Language REST Controller Tests")
class AddLanguageControllerTest {

    @Mock
    private AddLanguageUseCase addLanguageUseCase;

    @InjectMocks
    private AddLanguageController addLanguageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(addLanguageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ==================== MAIN FLOW TESTS ====================

    @Nested
    @DisplayName("Main Flow - Add Language")
    class MainFlowTests {

        @ParameterizedTest
        @CsvSource({
                "FR, French",
                "EN, English",
                "ES, Spanish",
                "Z, Zealandian",
                "CH, Chinese (Simplified)",
                "fr, French",
                "EN, '  English  '"
        })
        @DisplayName("Should successfully add language with various inputs")
        void testAddLanguageSuccessfully(String code, String label) throws Exception {
            // Given
            String jsonRequest = "{\"code\":\"" + code + "\",\"label\":\"" + label + "\"}";
            Language createdLanguage = new Language(code.toUpperCase(), label);
            when(addLanguageUseCase.execute(anyString(), anyString())).thenReturn(createdLanguage);

            // When & Then
            mockMvc.perform(post("/api/languages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value("" + code.toUpperCase() + "\""))
                    .andExpect(jsonPath("$.label").value(label.trim()))
                    .andExpect(header().exists("Location"))
                    .andExpect(header().stringValues("$.location", "/api/languages/" + code.toUpperCase()));

            verify(addLanguageUseCase, times(1)).execute(anyString(), anyString());
        }
    }

    // ==================== ALTERNATIVE FLOWS - VALIDATION ERRORS
    // ====================

    @Nested
    @DisplayName("Alternative Flows - Input Validation")
    class ValidationTests {

        @ParameterizedTest
        @MethodSource("provideInvalidJsonRequests")
        @DisplayName("Should return 400 Bad Request for invalid input")
        void testRejectInvalidInput(String jsonRequest) throws Exception {
            // When & Then
            mockMvc.perform(post("/api/languages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                    .andExpect(status().isBadRequest());

            // Use case should not be called
            verify(addLanguageUseCase, times(0)).execute(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body is malformed")
        void testRejectMalformedJson() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/languages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ invalid json"))
                    .andExpect(status().isBadRequest());
        }

        private static Stream<String> provideInvalidJsonRequests() {
            return Stream.of(
                    "{\"code\":null,\"label\":\"French\"}",
                    "{\"code\":\"FR\",\"label\":null}",
                    "{\"code\":\"\",\"label\":\"French\"}",
                    "{\"code\":\"FR\",\"label\":\"\"}");
        }
    }

    // ==================== USE CASE EXCEPTION HANDLING ====================

    @Nested
    @DisplayName("Alternative Flows - Use Case Errors")
    class UseCaseExceptionHandlingTests {

        @ParameterizedTest
        @MethodSource("provideIllegalArgumentExceptions")
        @DisplayName("Should return 400 Bad Request and error message for IllegalArgumentException")
        void testHandleIllegalArgumentException(String code, String label, String errorMessage) throws Exception {
            // Given
            String jsonRequest = "{\"code\":\"" + code + "\",\"label\":\"" + label + "\"}";
            doThrow(new IllegalArgumentException(errorMessage))
                    .when(addLanguageUseCase).execute(anyString(), anyString());

            // When & Then
            mockMvc.perform(post("/api/languages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("Should return 500 Internal Server Error on unexpected exception")
        void testHandleUnexpectedException() throws Exception {
            // Given
            String code = "FR";
            String label = "French";
            String jsonRequest = "{\"code\":\"" + code + "\",\"label\":\"" + label + "\"}";

            doThrow(new RuntimeException("Database connection failed"))
                    .when(addLanguageUseCase).execute(code, label);

            // When & Then
            mockMvc.perform(post("/api/languages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                    .andExpect(status().isInternalServerError());
        }

        private static Stream<Arguments> provideIllegalArgumentExceptions() {
            return Stream.of(
                    Arguments.of("FR", "French", "Language already exists"),
                    Arguments.of("EN", "English", "Language already exists"));
        }
    }

    // ==================== EDGE CASES ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle multiple add requests in sequence")
        void testMultipleAddRequests() throws Exception {
            // Given
            when(addLanguageUseCase.execute(anyString(), anyString()))
                    .thenReturn(new Language("FR", "French"))
                    .thenReturn(new Language("EN", "English"))
                    .thenReturn(new Language("ES", "Spanish"));

            // When & Then
            mockMvc.perform(post("/api/languages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"code\":\"FR\",\"label\":\"French\"}"))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/languages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"code\":\"EN\",\"label\":\"English\"}"))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/languages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"code\":\"ES\",\"label\":\"Spanish\"}"))
                    .andExpect(status().isCreated());

            verify(addLanguageUseCase, times(3)).execute(anyString(), anyString());
        }
    }
}
