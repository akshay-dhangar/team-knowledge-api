import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public final class AnswerApi {
    public static HttpServer start(int port, List<DocumentStore.Document> documents) throws IOException {
        var chunks = new ArrayList<SourceChunker.SourceChunk>();
        for (var document : List.copyOf(documents)) {
            chunks.addAll(SourceChunker.chunk(document.source(), document.text(), 500, 50));
        }
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/", exchange -> {
            try (exchange) {
                int status = 200;
                String body;
                if (!exchange.getRequestURI().getPath().equals("/answer")) {
                    status = 404;
                    body = "unknown path";
                } else if (!exchange.getRequestMethod().equals("POST")) {
                    status = 405;
                    exchange.getResponseHeaders().set("Allow", "POST");
                    body = "use POST";
                } else {
                    byte[] input = exchange.getRequestBody().readNBytes(4097);
                    if (input.length > 4096) {
                        status = 413;
                        body = "query exceeds 4096 bytes";
                    } else {
                        String query = new String(input, StandardCharsets.UTF_8);
                        var hits = LexicalSearch.search(chunks, query, 3);
                        var answer = new StringBuilder("mode lexical-extracts\n");
                        if (hits.isEmpty()) answer.append("No matching passages.\n");
                        for (var hit : hits) {
                            var source = hit.chunk().source();
                            var chunk = hit.chunk().chunk();
                            answer.append("[").append(source.documentId()).append("@")
                                .append(source.revision()).append(" ")
                                .append(chunk.startCodePoint()).append("-").append(chunk.endCodePoint())
                                .append("]\n").append(chunk.text()).append("\n");
                        }
                        body = answer.toString();
                    }
                }
                byte[] output = body.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(status, output.length);
                exchange.getResponseBody().write(output);
            }
        });
        server.start();
        return server;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 4) throw new IllegalArgumentException("usage: AnswerApi port directory documentId revision");
        var store = new DocumentStore(Path.of(args[1]));
        start(Integer.parseInt(args[0]), List.of(store.read(args[2], args[3])));
    }
}
