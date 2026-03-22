package mg.msys.abde_back.domain.model;

public record BookSearchCriteria(
        Integer publicationYear,
        String authorName,
        String language,
        String title,
        int page,
        int size) {

    private static final int MIN_PUBLICATION_YEAR = 0;
    private static final int MAX_PUBLICATION_YEAR = 9999;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public BookSearchCriteria(Integer publicationYear, String authorName, String language, String title) {
        this(publicationYear, authorName, language, title, DEFAULT_PAGE, DEFAULT_SIZE);
    }

    public BookSearchCriteria {
        validatePublicationYear(publicationYear);
        validatePage(page);
        validateSize(size);
        authorName = normalize(authorName);
        language = normalize(language);
        title = normalize(title);
    }

    public int offset() {
        return page * size;
    }

    private static void validatePublicationYear(Integer publicationYear) {
        if (publicationYear != null
                && (publicationYear < MIN_PUBLICATION_YEAR || publicationYear > MAX_PUBLICATION_YEAR)) {
            throw new IllegalArgumentException("Publication year must be between 0 and 9999");
        }
    }

    private static void validatePage(int page) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0");
        }
    }

    private static void validateSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}