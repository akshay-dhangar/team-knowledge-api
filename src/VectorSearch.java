import java.util.*;

public final class VectorSearch {
    @FunctionalInterface
    public interface EmbeddingProvider {
        double[] embed(String text);
    }

    public record Hit(SourceChunker.SourceChunk chunk, double cosine) {}

    private static double[] normalize(double[] input) {
        if (input == null || input.length == 0) throw new IllegalArgumentException("empty embedding");
        double scale = 0;
        for (double value : input) {
            if (!Double.isFinite(value)) throw new IllegalArgumentException("nonfinite embedding");
            scale = Math.max(scale, Math.abs(value));
        }
        if (scale == 0) throw new IllegalArgumentException("zero embedding");
        double[] vector = input.clone();
        double squared = 0;
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= scale;
            squared += vector[i] * vector[i];
        }
        double norm = Math.sqrt(squared);
        for (int i = 0; i < vector.length; i++) vector[i] /= norm;
        return vector;
    }

    public static List<Hit> search(List<SourceChunker.SourceChunk> chunks, String query,
                                   int limit, EmbeddingProvider provider) {
        if (limit < 1) throw new IllegalArgumentException("limit must be positive");
        double[] queryVector = normalize(provider.embed(Objects.requireNonNull(query)));
        var hits = new ArrayList<Hit>();
        for (var chunk : chunks) {
            double[] vector = normalize(provider.embed(chunk.chunk().text()));
            if (vector.length != queryVector.length) throw new IllegalArgumentException("embedding dimensions differ");
            double score = 0;
            for (int i = 0; i < vector.length; i++) score += vector[i] * queryVector[i];
            hits.add(new Hit(chunk, Math.max(-1, Math.min(1, score))));
        }
        hits.sort(Comparator.comparingDouble(Hit::cosine).reversed()
            .thenComparing(hit -> hit.chunk().source().documentId())
            .thenComparing(hit -> hit.chunk().source().revision())
            .thenComparingInt(hit -> hit.chunk().chunk().index()));
        return List.copyOf(hits.subList(0, Math.min(limit, hits.size())));
    }
}
