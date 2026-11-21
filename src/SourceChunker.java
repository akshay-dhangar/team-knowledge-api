import java.util.List;

/** Keeps citation coordinates tied to a caller-supplied document revision. */
public final class SourceChunker {
    public record Source(String documentId, String revision, String title) {
        public Source {
            requireText(documentId, "documentId");
            requireText(revision, "revision");
            requireText(title, "title");
        }
    }

    public record SourceChunk(Source source, TextChunker.Chunk chunk) {}

    public static List<SourceChunk> chunk(Source source, String text, int size, int overlap) {
        if (source == null || text == null) {
            throw new IllegalArgumentException("source and text required");
        }
        return TextChunker.chunk(text, size, overlap).stream()
            .map(chunk -> new SourceChunk(source, chunk)).toList();
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " required");
        }
    }
}
