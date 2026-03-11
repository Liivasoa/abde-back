package mg.msys.abde_back.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Author {
    private final String name;
    private final Integer birthYear;
    private final Integer deathYear;

    public Author(String name, Integer birthYear, Integer deathYear) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be null or empty");
        }
        this.name = name;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
    }
}