package mg.msys.abde_back.application.port;

import java.util.List;

import mg.msys.abde_back.domain.model.BookSearchCriteria;
import mg.msys.abde_back.domain.model.BookSearchResult;
import mg.msys.abde_back.domain.model.PaginatedResult;

public interface BookSearchPersistencePort {

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