import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

public final class CorpusEvaluation {
    private static DocumentStore.Document document(String id, String text) {
        return new DocumentStore.Document(new SourceChunker.Source(id, "v1", id), text);
    }

    public static void main(String[] args) throws Exception {
        var corpus = new ArrayList<>(List.of(
            document("deploy", "Rollback deployment using the previous release."),
            document("oncall", "Escalate incidents to the oncall engineer."),
            document("remote", "Remote meetings use a shared agenda.")));
        var server = AnswerApi.start(0, corpus);
        // Changes to the caller's list must not widen a running server's snapshot.
        corpus.add(document("private", "Secret payroll marker."));
        try {
            var client = HttpClient.newHttpClient();
            URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/answer");
            var cases = Map.of("rollback deployment", "deploy", "oncall incidents", "oncall",
                "remote agenda", "remote", "payroll", "", "astronomy", "");
            for (var entry : cases.entrySet()) {
                var request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(entry.getKey())).build();
                var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) throw new AssertionError("request failed");
                String body = response.body();
                if (entry.getValue().isEmpty()) {
                    if (!body.equals("mode lexical-extracts\nNo matching passages.\n"))
                        throw new AssertionError("unexpected passage for " + entry.getKey());
                } else if (!body.startsWith("mode lexical-extracts\n[" + entry.getValue() + "@v1 ")) {
                    throw new AssertionError("wrong top passage for " + entry.getKey());
                }
                if (body.contains("payroll marker") || body.contains("[private@"))
                    throw new AssertionError("excluded document exposed");
            }
            System.out.println("PASS 3/3 curated top-passage queries; 2/2 no-match queries");
            System.out.println("PASS caller-list mutation does not widen startup corpus");
        } finally {
            server.stop(0);
        }
    }
}
