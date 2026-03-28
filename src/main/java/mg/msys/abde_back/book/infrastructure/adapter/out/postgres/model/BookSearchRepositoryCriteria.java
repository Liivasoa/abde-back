package mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model;

public record BookSearchRepositoryCriteria(
        Integer publicationYear,
        String authorName,
        String language,
        String title,
        int page,
        int size) {

    public int offset() {
        return page * size;
    }
}