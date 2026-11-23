import java.util.*;

public final class VectorFixtures {
    public static void main(String[] args) {
        var source = new SourceChunker.Source("guide", "v1", "Guide");
        var chunks = SourceChunker.chunk(source, "ab", 1, 0);
        VectorSearch.EmbeddingProvider fixture = text -> switch (text) {
            case "query" -> new double[]{Double.MAX_VALUE, 0};
            case "a" -> new double[]{0, 1};
            default -> new double[]{1, 0};
        };
        var hits = VectorSearch.search(chunks, "query", 2, fixture);
        if (!hits.get(0).chunk().chunk().text().equals("b") ||
                hits.get(0).cosine() != 1 || hits.get(1).cosine() != 0)
            throw new AssertionError("cosine ranking failed");
        for (double[] invalid : new double[][]{ {}, {0, 0}, {Double.NaN, 1}, {Double.POSITIVE_INFINITY} }) {
            try {
                VectorSearch.search(chunks, "query", 1, text -> invalid);
                throw new AssertionError("invalid vector accepted");
            } catch (IllegalArgumentException expected) {}
        }
        try {
            VectorSearch.search(chunks, "query", 1, text -> text.equals("query") ? new double[]{1} : new double[]{1, 0});
            throw new AssertionError("dimension mismatch accepted");
        } catch (IllegalArgumentException expected) {}
        System.out.println("Vector ranking and validation fixtures passed");
    }
}
