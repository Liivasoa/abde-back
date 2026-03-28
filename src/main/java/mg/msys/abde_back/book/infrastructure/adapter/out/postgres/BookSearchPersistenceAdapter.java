package mg.msys.abde_back.book.infrastructure.adapter.out.postgres;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.book.application.port.out.BookPersistencePort;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.mapper.BookSearchCriteriaMapper;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.mapper.BookSearchResultMapper;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model.BookSearchRepositoryCriteria;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model.BookSearchRepositoryResult;
import mg.msys.abde_back.shared.application.port.in.query.dto.PaginatedResult;

@Component
@RequiredArgsConstructor
public class BookSearchPersistenceAdapter implements BookPersistencePort {
    private final BookSearchJpaRepository bookSearchJpaRepository;
    private final BookSearchCriteriaMapper bookSearchCriteriaMapper;
    private final BookSearchResultMapper bookSearchResultMapper;

    @Override
    public PaginatedResult<BookSearchResult> search(BookSearchCriteria criteria) {
        BookSearchRepositoryCriteria repositoryCriteria = bookSearchCriteriaMapper.toRepositoryCriteria(criteria);
        PaginatedResult<BookSearchRepositoryResult> repositoryResult = bookSearchJpaRepository
                .search(repositoryCriteria);
        return bookSearchResultMapper.toApplicationPage(repositoryResult);
    }
}