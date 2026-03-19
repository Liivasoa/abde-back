package mg.msys.abde_back.batch.entity;

import java.util.Map;
import java.util.Set;

public record AuthorAggregation(Map<String, Author> uniqueAuthors, Set<BookAuthorLink> links) {
}
