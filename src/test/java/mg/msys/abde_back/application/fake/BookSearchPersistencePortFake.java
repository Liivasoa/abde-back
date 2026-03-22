package mg.msys.abde_back.application.fake;

import java.util.ArrayList;
import java.util.List;

import mg.msys.abde_back.application.port.BookSearchPersistencePort;
import mg.msys.abde_back.domain.model.Book;
import mg.msys.abde_back.domain.model.BookSearchCriteria;
import mg.msys.abde_back.domain.model.PaginatedResult;

public class BookSearchPersistencePortFake implements BookSearchPersistencePort {

    private final List<Book> booksToReturn = new ArrayList<>();
    private long totalElementsToReturn;
    private BookSearchCriteria lastCriteria;
    private int callCount;

    @Override
    public List<Book> search(BookSearchCriteria criteria) {
        registerCall(criteria);
        return List.copyOf(booksToReturn);
    }

    @Override
    public PaginatedResult<Book> searchPage(BookSearchCriteria criteria) {
        registerCall(criteria);
        return PaginatedResult.of(List.copyOf(booksToReturn), criteria.page(), criteria.size(), totalElementsToReturn);
    }

    private void registerCall(BookSearchCriteria criteria) {
        this.lastCriteria = criteria;
        this.callCount++;
    }

    public void setBooksToReturn(List<Book> books) {
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
}