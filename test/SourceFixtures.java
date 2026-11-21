import java.util.List;

public final class SourceFixtures {
    public static void main(String[] args) {
        var source = new SourceChunker.Source("runbook", "v2", "Remote on-call");
        var chunks = SourceChunker.chunk(source, "A😀BCD", 3, 1);
        var expected = List.of(
            new SourceChunker.SourceChunk(source, new TextChunker.Chunk(0, 0, 3, "A😀B")),
            new SourceChunker.SourceChunk(source, new TextChunker.Chunk(1, 2, 5, "BCD")));
        if (!chunks.equals(expected)) throw new AssertionError("citation coordinates or source lost");
        var updated = new SourceChunker.Source("runbook", "v3", "Remote on-call");
        if (SourceChunker.chunk(updated, "A😀BCD", 3, 1).get(0).equals(chunks.get(0)))
            throw new AssertionError("document revisions conflated");
        try {
            new SourceChunker.Source("runbook", " ", "title");
            throw new AssertionError("missing revision accepted");
        } catch (IllegalArgumentException expectedError) {}
        if (!SourceChunker.chunk(source, "", 3, 1).isEmpty())
            throw new AssertionError("empty source produced citations");
        System.out.println("Source citation fixtures passed");
    }
}
