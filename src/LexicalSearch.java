import java.util.*;
import java.util.regex.Pattern;

public final class LexicalSearch {
    private static final Pattern WORD = Pattern.compile("[\\p{L}\\p{N}]+");
    public record Hit(SourceChunker.SourceChunk chunk, int matchedTerms) {}

    private static Set<String> terms(String text) {
        var words = new HashSet<String>();
        var matcher = WORD.matcher(Objects.requireNonNull(text).toLowerCase(Locale.ROOT));
        while (matcher.find()) words.add(matcher.group());
        return words;
    }

    /** Scores each supplied chunk by distinct query terms present in its text. */
    public static List<Hit> search(List<SourceChunker.SourceChunk> chunks, String query, int limit) {
        if (limit < 1) throw new IllegalArgumentException("limit must be positive");
        var queryTerms = terms(query);
        var hits = new ArrayList<Hit>();
        for (var chunk : chunks) {
            var present = terms(chunk.chunk().text());
            present.retainAll(queryTerms);
            if (!present.isEmpty()) hits.add(new Hit(chunk, present.size()));
        }
        hits.sort(Comparator.comparingInt(Hit::matchedTerms).reversed()
            .thenComparing(hit -> hit.chunk().source().documentId())
            .thenComparing(hit -> hit.chunk().source().revision())
            .thenComparingInt(hit -> hit.chunk().chunk().index()));
        return List.copyOf(hits.subList(0, Math.min(limit, hits.size())));
    }
}
