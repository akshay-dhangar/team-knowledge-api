import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.List;

public final class AnswerFixtures {
    public static void main(String[] args) throws Exception {
        var document = new DocumentStore.Document(new SourceChunker.Source("guide", "v1", "Guide"), "Rollback safely.");
        var server = AnswerApi.start(0, List.of(document));
        try {
            var client = HttpClient.newHttpClient();
            var uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/answer");
            for (String query : new String[]{"rollback", "unknown", "x".repeat(4097)}) {
                var request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(query)).build();
                var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String expected = switch (query) {
                    case "rollback" -> "mode lexical-extracts\n[guide@v1 0-16]\nRollback safely.\n";
                    case "unknown" -> "mode lexical-extracts\nNo matching passages.\n";
                    default -> "query exceeds 4096 bytes";
                };
                if (response.statusCode() != (query.length() > 4096 ? 413 : 200) || !response.body().equals(expected))
                    throw new AssertionError(response.body());
            }
        } finally {
            server.stop(0);
        }
        System.out.println("Cited answer HTTP fixtures passed");
    }
}
