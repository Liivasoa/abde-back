package mg.msys.abde_back.language.application.port.in.query.dto;

public record LanguageBookCountResult(String code, String label, long bookCount) {

    public LanguageBookCountResult {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Language code cannot be null or empty");
        }
        if (bookCount < 0) {
            throw new IllegalArgumentException("Book count must be >= 0");
        }
        code = code.trim().toUpperCase();
        String trimmedLabel = (label == null) ? null : label.trim();
        label = (trimmedLabel == null || trimmedLabel.isEmpty()) ? code : trimmedLabel;
    }
}
