package mg.msys.abde_back.book.infrastructure.adapter.out.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.mapper.BookSearchCriteriaMapper;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.mapper.BookSearchResultMapper;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model.BookSearchRepositoryCriteria;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model.BookSearchRepositoryResult;
import mg.msys.abde_back.shared.application.port.in.query.dto.PaginatedResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Adapter/Out] Book Search Persistence Adapter Mapping Tests")
class BookSearchPersistenceAdapterTest {

    @Mock
    private BookSearchJpaRepository repository;

    @Mock
    private BookSearchCriteriaMapper criteriaMapper;

    @Mock
    private BookSearchResultMapper resultMapper;

    @InjectMocks
    private BookSearchPersistenceAdapter adapter;

    @Test
    @DisplayName("Should delegate criteria mapping, repository search and page mapping")
    void testSearchMapsCriteriaAndResult() {
        BookSearchCriteria criteria = new BookSearchCriteria(1949, "Orwell", "EN", "Nineteen", 2, 5);
        BookSearchRepositoryCriteria repositoryCriteria = new BookSearchRepositoryCriteria(1949, "Orwell", "EN",
                "Nineteen", 2, 5);

        BookSearchRepositoryResult repositoryItem = new BookSearchRepositoryResult(
                2L,
                "Nineteen Eighty-Four",
                null,
                "EN",
                List.of(new BookSearchRepositoryResult.AuthorReference(11L, "George Orwell")));

        PaginatedResult<BookSearchRepositoryResult> repositoryPage = PaginatedResult.of(List.of(repositoryItem), 2, 5,
                13);
        PaginatedResult<BookSearchResult> applicationPage = PaginatedResult.of(List.of(new BookSearchResult(
                2L,
                "Nineteen Eighty-Four",
                null,
                "EN",
                List.of(new BookSearchResult.AuthorReference(11L, "George Orwell")))), 2, 5, 13);

        when(criteriaMapper.toRepositoryCriteria(criteria)).thenReturn(repositoryCriteria);
        when(repository.search(repositoryCriteria)).thenReturn(repositoryPage);
        when(resultMapper.toApplicationPage(repositoryPage)).thenReturn(applicationPage);

        PaginatedResult<BookSearchResult> result = adapter.search(criteria);

        ArgumentCaptor<BookSearchRepositoryCriteria> criteriaCaptor = ArgumentCaptor
                .forClass(BookSearchRepositoryCriteria.class);
        verify(repository).search(criteriaCaptor.capture());
        verify(criteriaMapper).toRepositoryCriteria(criteria);
        verify(resultMapper).toApplicationPage(repositoryPage);

        BookSearchRepositoryCriteria capturedCriteria = criteriaCaptor.getValue();
        assertThat(capturedCriteria.publicationYear()).isEqualTo(1949);
        assertThat(capturedCriteria.authorName()).isEqualTo("Orwell");
        assertThat(capturedCriteria.language()).isEqualTo("EN");
        assertThat(capturedCriteria.title()).isEqualTo("Nineteen");
        assertThat(capturedCriteria.page()).isEqualTo(2);
        assertThat(capturedCriteria.size()).isEqualTo(5);
        assertThat(capturedCriteria.offset()).isEqualTo(10);
        assertThat(result).isSameAs(applicationPage);
    }
}
