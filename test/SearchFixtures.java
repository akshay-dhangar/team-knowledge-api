import java.util.*;

public final class SearchFixtures {
    public static void main(String[] args) {
        var runbook = new SourceChunker.Source("runbook", "v1", "Deploy guide");
        var policy = new SourceChunker.Source("policy", "v2", "Remote work");
        var chunks = new ArrayList<SourceChunker.SourceChunk>();
        chunks.addAll(SourceChunker.chunk(runbook, "Deploy rollback safely.", 100, 0));
        chunks.addAll(SourceChunker.chunk(policy, "Deploy deploy remotely.", 100, 0));
        var hits = LexicalSearch.search(chunks, "DEPLOY rollback rollback", 1);
        if (hits.size() != 1 || hits.get(0).matchedTerms() != 2 ||
                !hits.get(0).chunk().source().equals(runbook))
            throw new AssertionError("distinct-term ranking or citation lost");
        if (!LexicalSearch.search(chunks, "deployment", 10).isEmpty())
            throw new AssertionError("substring matched");
        if (!LexicalSearch.search(chunks, "?!", 10).isEmpty())
            throw new AssertionError("empty query matched");
        var before = LexicalSearch.search(chunks, "deploy", 10);
        Collections.reverse(chunks);
        if (!before.equals(LexicalSearch.search(chunks, "deploy", 10)))
            throw new AssertionError("tie order depends on corpus order");
        System.out.println("Lexical ranking and citation fixtures passed");
    }
}
