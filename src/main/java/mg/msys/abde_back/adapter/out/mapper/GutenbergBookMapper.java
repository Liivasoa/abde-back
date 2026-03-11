package mg.msys.abde_back.adapter.out.mapper;

import mg.msys.abde_back.adapter.out.dto.GutenbergAuthorDto;
import mg.msys.abde_back.adapter.out.dto.GutenbergBookDto;
import mg.msys.abde_back.domain.model.Author;
import mg.msys.abde_back.domain.model.Book;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GutenbergBookMapper {

    public Book toDomain(GutenbergBookDto dto) {
        List<Author> authors = dto.authors() == null ? List.of()
                : dto.authors().stream().map(this::toDomain).toList();

        return new Book(
                dto.id(),
                dto.title(),
                dto.summaries(),
                dto.languages(),
                dto.formats(),
                authors,
                dto.downloadCount());
    }

    public Author toDomain(GutenbergAuthorDto dto) {
        return new Author(dto.name(), dto.birthYear(), dto.deathYear());
    }
}
