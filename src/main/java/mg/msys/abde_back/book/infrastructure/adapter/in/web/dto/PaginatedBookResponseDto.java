package mg.msys.abde_back.book.infrastructure.adapter.in.web.dto;

import java.util.List;

public record PaginatedBookResponseDto(
        List<BookResponseDto> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious) {
}
