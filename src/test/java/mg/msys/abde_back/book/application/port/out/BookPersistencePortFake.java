package mg.msys.abde_back.book.application.port.out;

import java.util.ArrayList;
import java.util.List;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchResult;
import mg.msys.abde_back.domain.model.PaginatedResult;

public class BookPersistencePortFake implements BookPersistencePort {

    private final List<BookSearchResult> booksToReturn = new ArrayList<>();
    private long totalElementsToReturn;
    private BookSearchCriteria lastCriteria;
    private int callCount;
    private int searchCallCount;
    private int searchPageCallCount;

    @Override
    public List<BookSearchResult> search(BookSearchCriteria criteria) {
        registerCall(criteria);
        this.searchCallCount++;
        return List.copyOf(booksToReturn);
    }

    @Override
    public PaginatedResult<BookSearchResult> searchPage(BookSearchCriteria criteria) {
        registerCall(criteria);
        this.searchPageCallCount++;
        return PaginatedResult.of(List.copyOf(booksToReturn), criteria.page(), criteria.size(), totalElementsToReturn);
    }

    private void registerCall(BookSearchCriteria criteria) {
        this.lastCriteria = criteria;
        this.callCount++;
    }

    public void setBooksToReturn(List<BookSearchResult> books) {
        this.booksToReturn.clear();
        this.booksToReturn.addAll(books);
    }

    public BookSearchCriteria getLastCriteria() {
        return lastCriteria;
    }

    public void setTotalElementsToReturn(long totalElementsToReturn) {
        this.totalElementsToReturn = totalElementsToReturn;
    }

    public int getCallCount() {
        return callCount;
    }

    public int getSearchCallCount() {
        return searchCallCount;
    }

    public int getSearchPageCallCount() {
        return searchPageCallCount;
    }
}