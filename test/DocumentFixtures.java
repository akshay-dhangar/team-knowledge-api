import java.io.IOException;
import java.nio.file.*;

public final class DocumentFixtures {
    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("knowledge-store");
        try {
            var document = new DocumentStore.Document(new SourceChunker.Source("guide", "v1", "On-call"), "A😀B\nRunbook");
            new DocumentStore(directory).ingest(document);
            var restarted = new DocumentStore(directory);
            if (!restarted.read("guide", "v1").equals(document)) throw new AssertionError("restart lost data");
            restarted.ingest(document);
            try {
                restarted.ingest(new DocumentStore.Document(document.source(), "changed"));
                throw new AssertionError("revision overwritten");
            } catch (IOException expected) {}
            if (!restarted.read("guide", "v1").equals(document)) throw new AssertionError("conflict changed data");
            try {
                restarted.read("../guide", "v1");
                throw new AssertionError("path traversal accepted");
            } catch (IllegalArgumentException expected) {}
            System.out.println("Document restart and revision fixtures passed");
        } finally {
            try (var files = Files.list(directory)) {
                for (Path file : files.toList()) Files.delete(file);
            }
            Files.delete(directory);
        }
    }
}
