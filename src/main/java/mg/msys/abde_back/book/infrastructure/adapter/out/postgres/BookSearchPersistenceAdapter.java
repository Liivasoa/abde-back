package mg.msys.abde_back.book.infrastructure.adapter.out.postgres;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.book.application.port.out.BookPersistencePort;
import mg.msys.abde_back.domain.model.PaginatedResult;
import mg.msys.abde_back.infrastructure.repository.BookSearchJpaRepository;

@Component
@RequiredArgsConstructor
public class BookSearchPersistenceAdapter implements BookPersistencePort {
    private final BookSearchJpaRepository bookSearchJpaRepository;

    @Override
    public List<BookSearchResult> search(BookSearchCriteria criteria) {
        return bookSearchJpaRepository.search(criteria);
    }

    @Override
    public PaginatedResult<BookSearchResult> searchPage(BookSearchCriteria criteria) {
        return bookSearchJpaRepository.searchPage(criteria);
    }
}