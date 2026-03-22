package mg.msys.abde_back.application.port;

import java.util.List;

import mg.msys.abde_back.domain.model.Book;
import mg.msys.abde_back.domain.model.BookSearchCriteria;
import mg.msys.abde_back.domain.model.PaginatedResult;

public interface BookSearchPersistencePort {

    List<Book> search(BookSearchCriteria criteria);

    default PaginatedResult<Book> searchPage(BookSearchCriteria criteria) {
        List<Book> allItems = search(criteria);
        int fromIndex = Math.min(criteria.offset(), allItems.size());
        int toIndex = Math.min(fromIndex + criteria.size(), allItems.size());
        List<Book> items = allItems.subList(fromIndex, toIndex);
        return PaginatedResult.of(items, criteria.page(), criteria.size(), allItems.size());
    }
}