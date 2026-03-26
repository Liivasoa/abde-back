package mg.msys.abde_back.book.application.port.out;

import java.util.List;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.domain.model.PaginatedResult;

public interface BookPersistencePort {

    List<BookSearchResult> search(BookSearchCriteria criteria);

    default PaginatedResult<BookSearchResult> searchPage(BookSearchCriteria criteria) {
        List<BookSearchResult> allItems = search(criteria);
        int fromIndex = Math.min(criteria.offset(), allItems.size());
        int toIndex = Math.min(fromIndex + criteria.size(), allItems.size());
        List<BookSearchResult> items = allItems.subList(fromIndex, toIndex);
        return PaginatedResult.of(items, criteria.page(), criteria.size(), allItems.size());
    }

    default PaginatedResult<BookSearchResult> searchResultsPage(BookSearchCriteria criteria) {
        return searchPage(criteria);
    }
}