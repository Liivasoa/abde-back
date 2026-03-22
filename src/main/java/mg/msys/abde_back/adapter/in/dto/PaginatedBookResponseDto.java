package mg.msys.abde_back.adapter.in.dto;

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
