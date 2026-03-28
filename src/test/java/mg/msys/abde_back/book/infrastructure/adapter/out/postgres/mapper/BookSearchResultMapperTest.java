package mg.msys.abde_back.book.infrastructure.adapter.out.postgres.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model.BookSearchRepositoryResult;
import mg.msys.abde_back.shared.application.port.in.query.dto.PaginatedResult;

@DisplayName("[Mapper] Book Search Result Mapper Tests")
class BookSearchResultMapperTest {

    private final BookSearchResultMapper mapper = Mappers.getMapper(BookSearchResultMapper.class);

    @Test
    @DisplayName("Should map repository page to application page with author references")
    void testToApplicationPage() {
        BookSearchRepositoryResult repositoryItem = new BookSearchRepositoryResult(
                2L,
                "Nineteen Eighty-Four",
                LocalDate.of(1949, 6, 8),
                "EN",
                List.of(new BookSearchRepositoryResult.AuthorReference(11L, "George Orwell")));

        PaginatedResult<BookSearchRepositoryResult> repositoryPage = PaginatedResult.of(List.of(repositoryItem), 2, 5,
                13);

        PaginatedResult<BookSearchResult> mapped = mapper.toApplicationPage(repositoryPage);

        assertThat(mapped.page()).isEqualTo(2);
        assertThat(mapped.size()).isEqualTo(5);
        assertThat(mapped.totalElements()).isEqualTo(13);
        assertThat(mapped.items()).hasSize(1);

        BookSearchResult firstItem = mapped.items().get(0);
        assertThat(firstItem.id()).isEqualTo(2L);
        assertThat(firstItem.title()).isEqualTo("Nineteen Eighty-Four");
        assertThat(firstItem.issued()).isEqualTo(LocalDate.of(1949, 6, 8));
        assertThat(firstItem.language()).isEqualTo("EN");
        assertThat(firstItem.authors()).hasSize(1);
        assertThat(firstItem.authors().get(0).id()).isEqualTo(11L);
        assertThat(firstItem.authors().get(0).fullName()).isEqualTo("George Orwell");
    }
}