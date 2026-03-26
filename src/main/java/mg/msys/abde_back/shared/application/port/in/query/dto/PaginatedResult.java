package mg.msys.abde_back.shared.application.port.in.query.dto;

import java.util.List;

public final class PaginatedResult<T> {

    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public PaginatedResult(List<T> items, int page, int size, long totalElements, int totalPages, boolean hasNext,
            boolean hasPrevious) {
        this.items = items == null ? List.of() : List.copyOf(items);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public static <T> PaginatedResult<T> of(List<T> items, int page, int size, long totalElements) {
        int totalPages = totalElements == 0 ? 0 : (int) ((totalElements + size - 1) / size);
        boolean hasNext = page + 1 < totalPages;
        boolean hasPrevious = page > 0;
        return new PaginatedResult<>(items, page, size, totalElements, totalPages, hasNext, hasPrevious);
    }

    public List<T> items() {
        return items;
    }

    public int page() {
        return page;
    }

    public int size() {
        return size;
    }

    public long totalElements() {
        return totalElements;
    }

    public int totalPages() {
        return totalPages;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }
}
