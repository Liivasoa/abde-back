package mg.msys.abde_back.language.infrastructure.adapter.in.web;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import mg.msys.abde_back.language.application.port.in.query.ListAvailableLanguagesUseCase;
import mg.msys.abde_back.language.application.port.in.query.dto.LanguageBookCountResult;
import mg.msys.abde_back.shared.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;

@DisplayName("[Adapter/In] List Available Languages REST Controller Tests")
class ListAvailableLanguagesControllerTest {

    @Mock
    private ListAvailableLanguagesUseCase listAvailableLanguagesUseCase;

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

    @Test
    @DisplayName("Should return list of available languages with book count")
    void shouldReturnAvailableLanguages() throws Exception {
        when(listAvailableLanguagesUseCase.execute()).thenReturn(List.of(
                new LanguageBookCountResult("EN", "English", 12),
                new LanguageBookCountResult("FR", "Français", 3)));

        mockMvc.perform(get("/api/language"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("EN"))
                .andExpect(jsonPath("$[0].label").value("English"))
                .andExpect(jsonPath("$[0].bookCount").value(12))
                .andExpect(jsonPath("$[1].code").value("FR"))
                .andExpect(jsonPath("$[1].label").value("Français"))
                .andExpect(jsonPath("$[1].bookCount").value(3));

        verify(listAvailableLanguagesUseCase, times(1)).execute();
    }

    @Test
    @DisplayName("Should return empty array when no languages are available")
    void shouldReturnEmptyArrayWhenNoLanguages() throws Exception {
        when(listAvailableLanguagesUseCase.execute()).thenReturn(List.of());

        mockMvc.perform(get("/api/language"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(listAvailableLanguagesUseCase, times(1)).execute();
    }
}
