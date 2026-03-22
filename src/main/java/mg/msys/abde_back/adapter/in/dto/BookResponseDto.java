package mg.msys.abde_back.adapter.in.dto;

import java.time.LocalDate;
import java.util.List;

public record BookResponseDto(
                Long id,
                String title,
                LocalDate issued,
                String language,
                List<AuthorResponseDto> authors) {
}