package mg.msys.abde_back.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
public class Book {
    private final int id;
    private final String title;
    private final List<String> summaries;
    private final List<String> languages;
    private final Map<String, String> formats;
    private final List<Author> authors;
    private final int downloadCount;

    public Book(int id, String title, List<String> summaries,
            List<String> languages, Map<String, String> formats,
            List<Author> authors, int downloadCount) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be null or empty");
        }
        this.id = id;
        this.title = title;
        this.summaries = summaries != null ? summaries : List.of();
        this.languages = languages != null ? languages : List.of();
        this.formats = formats != null ? formats : Map.of();
        this.authors = authors != null ? authors : List.of();
        this.downloadCount = downloadCount;
    }
}