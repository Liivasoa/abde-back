package mg.msys.abde_back.book.infrastructure.adapter.out.postgres.mapper;

import org.mapstruct.Mapper;

import mg.msys.abde_back.book.application.port.in.query.dto.BookSearchCriteria;
import mg.msys.abde_back.book.infrastructure.adapter.out.postgres.model.BookSearchRepositoryCriteria;

@Mapper(componentModel = "spring")
public interface BookSearchCriteriaMapper {

    BookSearchRepositoryCriteria toRepositoryCriteria(BookSearchCriteria criteria);
}