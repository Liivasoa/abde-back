package mg.msys.abde_back.language.infrastructure.adapter.in.web;

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
import mg.msys.abde_back.language.in.web.LanguageController;
import mg.msys.abde_back.shared.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;

@DisplayName("[Adapter/In] Add Language REST Controller Tests")
class AddLanguageControllerTest {

        @Mock
        private AddLanguageUseCase addLanguageUseCase;

        @InjectMocks
        private LanguageController languageController;

        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                this.mockMvc = MockMvcBuilders.standaloneSetup(languageController)
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
                        String jsonRequest = "{\"code\":\"" + code + "\",\"label\":\"" + label + "\"}";
                        Language createdLanguage = new Language(code.toUpperCase(), label);
                        when(addLanguageUseCase.execute(anyString(), anyString())).thenReturn(createdLanguage);

                        mockMvc.perform(post("/api/language")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.id").value(code.toUpperCase()))
                                        .andExpect(header().exists("Location"));

                        verify(addLanguageUseCase, times(1)).execute(anyString(), anyString());
                }
        }

        // ==================== ALTERNATIVE FLOWS - VALIDATION ERRORS
        // ====================

        @Nested
        @DisplayName("Alternative Flows - Input Validation")
        class ValidationTests {

                @ParameterizedTest
                @MethodSource("provideInvalidInputs")
                @DisplayName("Should return 400 Bad Request when domain enforces business rules")
                void testRejectInvalidInput(String code, String label) throws Exception {
                        String jsonRequest = "{\"code\":" + (code == null ? "null" : "\"" + code + "\"") +
                                        ",\"label\":" + (label == null ? "null" : "\"" + label + "\"") + "}";
                        doThrow(new IllegalArgumentException("Language code cannot be null or empty"))
                                        .when(addLanguageUseCase).execute(code, label);

                        mockMvc.perform(post("/api/language")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest))
                                        .andExpect(status().isBadRequest());

                        verify(addLanguageUseCase, times(1)).execute(code, label);
                }

                @Test
                @DisplayName("Should return 400 Bad Request when request body is malformed")
                void testRejectMalformedJson() throws Exception {
                        mockMvc.perform(post("/api/language")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{ invalid json"))
                                        .andExpect(status().isBadRequest());
                }

                private static Stream<Arguments> provideInvalidInputs() {
                        return Stream.of(
                                        Arguments.of(null, "French"),
                                        Arguments.of("FR", null),
                                        Arguments.of("", "French"),
                                        Arguments.of("FR", ""));
                }
        }

        // ==================== USE CASE EXCEPTION HANDLING ====================

        @Nested
        @DisplayName("Alternative Flows - Use Case Errors")
        class UseCaseExceptionHandlingTests {

                @ParameterizedTest
                @MethodSource("provideIllegalArgumentExceptions")
                @DisplayName("Should return 400 Bad Request and error message for IllegalArgumentException")
                void testHandleIllegalArgumentException(String code, String label, String errorMessage)
                                throws Exception {
                        String jsonRequest = "{\"code\":\"" + code + "\",\"label\":\"" + label + "\"}";
                        doThrow(new IllegalArgumentException(errorMessage))
                                        .when(addLanguageUseCase).execute(anyString(), anyString());

                        mockMvc.perform(post("/api/language")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.message").exists());
                }

                @Test
                @DisplayName("Should return 500 Internal Server Error on unexpected exception")
                void testHandleUnexpectedException() throws Exception {
                        String code = "FR";
                        String label = "French";
                        String jsonRequest = "{\"code\":\"" + code + "\",\"label\":\"" + label + "\"}";

                        doThrow(new RuntimeException("Database connection failed"))
                                        .when(addLanguageUseCase).execute(code, label);

                        mockMvc.perform(post("/api/language")
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
                        when(addLanguageUseCase.execute(anyString(), anyString()))
                                        .thenReturn(new Language("FR", "French"))
                                        .thenReturn(new Language("EN", "English"))
                                        .thenReturn(new Language("ES", "Spanish"));

                        mockMvc.perform(post("/api/language")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"code\":\"FR\",\"label\":\"French\"}"))
                                        .andExpect(status().isCreated());

                        mockMvc.perform(post("/api/language")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"code\":\"EN\",\"label\":\"English\"}"))
                                        .andExpect(status().isCreated());

                        mockMvc.perform(post("/api/language")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"code\":\"ES\",\"label\":\"Spanish\"}"))
                                        .andExpect(status().isCreated());

                        verify(addLanguageUseCase, times(3)).execute(anyString(), anyString());
                }
        }
}
