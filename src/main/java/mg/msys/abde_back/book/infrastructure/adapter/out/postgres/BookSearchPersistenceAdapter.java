package mg.msys.abde_back.book.infrastructure.adapter.out.postgres;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.book.application.port.out.BookPersistencePort;
import mg.msys.abde_back.shared.application.port.in.query.dto.PaginatedResult;

@Component
@RequiredArgsConstructor
public class BookSearchPersistenceAdapter implements BookPersistencePort {
    private final BookSearchJpaRepository bookSearchJpaRepository;

    @Override
    public PaginatedResult<BookSearchResult> search(BookSearchCriteria criteria) {
        return bookSearchJpaRepository.search(criteria);
    }
}