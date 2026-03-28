package mg.msys.abde_back.book.infrastructure.adapter.out.postgres.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model.BookSearchRepositoryResult;
import mg.msys.abde_back.shared.application.port.in.query.dto.PaginatedResult;

@Mapper(componentModel = "spring")
public interface BookSearchResultMapper {

    BookSearchResult toApplicationResult(BookSearchRepositoryResult repositoryResult);

    List<BookSearchResult.AuthorReference> toApplicationAuthors(
            List<BookSearchRepositoryResult.AuthorReference> repositoryAuthors);

    BookSearchResult.AuthorReference toApplicationAuthor(BookSearchRepositoryResult.AuthorReference repositoryAuthor);

    default PaginatedResult<BookSearchResult> toApplicationPage(
            PaginatedResult<BookSearchRepositoryResult> repositoryResult) {
        List<BookSearchResult> items = repositoryResult.items().stream()
                .map(this::toApplicationResult)
                .toList();

        return PaginatedResult.of(items, repositoryResult.page(), repositoryResult.size(),
                repositoryResult.totalElements());
    }
}