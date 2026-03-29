package mg.msys.abde_back.language.application.port.in.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mg.msys.abde_back.language.application.port.in.query.dto.LanguageBookCountResult;
import mg.msys.abde_back.language.application.port.in.query.handler.ListAvailableLanguagesHandler;
import mg.msys.abde_back.language.application.port.out.LanguagePersistencePortFake;

@DisplayName("[Application/Use case] List Available Languages Use Case Tests")
class ListAvailableLanguagesUseCaseTest {

    private ListAvailableLanguagesUseCase useCase;
    private LanguagePersistencePortFake fakePort;

    @BeforeEach
    void setUp() {
        fakePort = new LanguagePersistencePortFake();
        useCase = new ListAvailableLanguagesHandler(fakePort);
    }

    @Test
    @DisplayName("Should return list of available languages with book count from port")
    void shouldReturnListFromPort() {
        List<LanguageBookCountResult> languages = List.of(
                new LanguageBookCountResult("FR", "Français", 12),
                new LanguageBookCountResult("EN", "English", 5));
        fakePort.setAvailableLanguagesToReturn(languages);

        List<LanguageBookCountResult> result = useCase.execute();

        assertEquals(2, result.size());
        assertEquals(1, fakePort.getAvailableLanguagesCallCount());
        assertEquals("FR", result.get(0).code());
        assertEquals(12, result.get(0).bookCount());
    }

    @Test
    @DisplayName("Should return empty list when no languages available")
    void shouldReturnEmptyListWhenNoLanguages() {
        fakePort.setAvailableLanguagesToReturn(List.of());

        List<LanguageBookCountResult> result = useCase.execute();

        assertTrue(result.isEmpty());
        assertEquals(1, fakePort.getAvailableLanguagesCallCount());
    }
}
