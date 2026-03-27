package mg.msys.abde_back.book.application.port.out;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.shared.application.port.in.query.dto.PaginatedResult;

public interface BookPersistencePort {

    PaginatedResult<BookSearchResult> search(BookSearchCriteria criteria);
}