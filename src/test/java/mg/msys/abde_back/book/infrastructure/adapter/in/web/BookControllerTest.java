package mg.msys.abde_back.book.infrastructure.adapter.in.web;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import mg.msys.abde_back.application.usecase.book.SearchBooksUseCase;
import mg.msys.abde_back.domain.model.BookSearchResult;
import mg.msys.abde_back.domain.model.PaginatedResult;
import mg.msys.abde_back.shared.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;

@DisplayName("[Adapter/In] Book Controller Tests")
class BookControllerTest {

        @Mock
        private SearchBooksUseCase searchBooksUseCase;

        @InjectMocks
        private BookController bookController;

        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                this.mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
        }

        @Test
        @DisplayName("Should return paginated matching books with all query params")
        void testSearchBooksWithAllFilters() throws Exception {
                when(searchBooksUseCase.execute(1851, "Melville", "EN", "Moby", 0, 10))
                                .thenReturn(PaginatedResult.of(
                                                List.of(new BookSearchResult(1L, "Moby Dick", LocalDate.of(1851, 1, 1),
                                                                "EN",
                                                                List.of(new BookSearchResult.AuthorReference(10L,
                                                                                "Herman Melville")))),
                                                0,
                                                10,
                                                1));

                mockMvc.perform(get("/api/book/search")
                                .param("publicationYear", "1851")
                                .param("authorName", "Melville")
                                .param("language", "EN")
                                .param("title", "Moby")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.items[0].id").value(1))
                                .andExpect(jsonPath("$.items[0].title").value("Moby Dick"))
                                .andExpect(jsonPath("$.items[0].language").value("EN"))
                                .andExpect(jsonPath("$.items[0].authors[0].id").value(10))
                                .andExpect(jsonPath("$.items[0].authors[0].fullName").value("Herman Melville"))
                                .andExpect(jsonPath("$.page").value(0))
                                .andExpect(jsonPath("$.size").value(10))
                                .andExpect(jsonPath("$.totalElements").value(1))
                                .andExpect(jsonPath("$.totalPages").value(1));

                verify(searchBooksUseCase, times(1)).execute(1851, "Melville", "EN", "Moby", 0, 10);
        }

        @Test
        @DisplayName("Should return paginated books when no params are provided")
        void testSearchBooksWithoutFilters() throws Exception {
                when(searchBooksUseCase.execute(null, null, null, null, 0, 20))
                                .thenReturn(PaginatedResult.of(
                                                List.of(
                                                                new BookSearchResult(1L, "Moby Dick",
                                                                                LocalDate.of(1851, 1, 1), "EN",
                                                                                List.of(new BookSearchResult.AuthorReference(
                                                                                                10L,
                                                                                                "Herman Melville"))),
                                                                new BookSearchResult(3L, "Le Petit Prince",
                                                                                LocalDate.of(1932, 1, 1), "FR",
                                                                                List.of(new BookSearchResult.AuthorReference(
                                                                                                12L,
                                                                                                "Antoine de Saint-Exupery")))),
                                                0,
                                                20,
                                                2));

                mockMvc.perform(get("/api/book/search"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.items.length()").value(2))
                                .andExpect(jsonPath("$.page").value(0))
                                .andExpect(jsonPath("$.size").value(20))
                                .andExpect(jsonPath("$.totalElements").value(2));

                verify(searchBooksUseCase, times(1)).execute(null, null, null, null, 0, 20);
        }
}