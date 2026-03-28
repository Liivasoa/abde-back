package mg.msys.abde_back.book.infrastructure.adapter.out.postgres.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model.BookSearchRepositoryCriteria;

@DisplayName("[Mapper] Book Search Criteria Mapper Tests")
class BookSearchCriteriaMapperTest {

    private final BookSearchCriteriaMapper mapper = Mappers.getMapper(BookSearchCriteriaMapper.class);

    @Test
    @DisplayName("Should map application criteria to repository criteria")
    void testToRepositoryCriteria() {
        BookSearchCriteria criteria = new BookSearchCriteria(1949, "Orwell", "EN", "Nineteen", 2, 5);

        BookSearchRepositoryCriteria mapped = mapper.toRepositoryCriteria(criteria);

        assertThat(mapped.publicationYear()).isEqualTo(1949);
        assertThat(mapped.authorName()).isEqualTo("Orwell");
        assertThat(mapped.language()).isEqualTo("EN");
        assertThat(mapped.title()).isEqualTo("Nineteen");
        assertThat(mapped.page()).isEqualTo(2);
        assertThat(mapped.size()).isEqualTo(5);
        assertThat(mapped.offset()).isEqualTo(10);
    }
}