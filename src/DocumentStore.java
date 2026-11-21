import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.Objects;

/** Local immutable revisions. Store directories must be owned by the caller. */
public final class DocumentStore {
    public record Document(SourceChunker.Source source, String text) {
        public Document {
            Objects.requireNonNull(source);
            Objects.requireNonNull(text);
        }
    }

    private final Path directory;

    public DocumentStore(Path directory) throws IOException {
        this.directory = directory;
        Files.createDirectories(directory);
    }

    private Path file(String id, String revision) {
        if (!id.matches("[A-Za-z0-9_-]{1,64}") || !revision.matches("[A-Za-z0-9_-]{1,64}"))
            throw new IllegalArgumentException("IDs and revisions require 1–64 ASCII letters, digits, _ or -");
        return directory.resolve(id + "." + revision + ".doc");
    }

    public Document read(String id, String revision) throws IOException {
        try (var input = new DataInputStream(Files.newInputStream(file(id, revision)))) {
            if (input.readInt() != 1) throw new IOException("unsupported document format");
            var source = new SourceChunker.Source(input.readUTF(), input.readUTF(), input.readUTF());
            var document = new Document(source, input.readUTF());
            if (!source.documentId().equals(id) || !source.revision().equals(revision) || input.read() != -1)
                throw new IOException("document identity or trailing data invalid");
            return document;
        }
    }

    public synchronized void ingest(Document document) throws IOException {
        Path target = file(document.source().documentId(), document.source().revision());
        // Encode before touching persistent state. writeUTF bounds each field to 65535 encoded bytes.
        var buffer = new ByteArrayOutputStream();
        try (var output = new DataOutputStream(buffer)) {
            output.writeInt(1);
            output.writeUTF(document.source().documentId());
            output.writeUTF(document.source().revision());
            output.writeUTF(document.source().title());
            output.writeUTF(document.text());
        }
        try (var channel = FileChannel.open(directory.resolve(".lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             var lock = channel.lock()) {
            if (!Files.notExists(target)) {
                if (!read(document.source().documentId(), document.source().revision()).equals(document))
                    throw new IOException("revision already contains different content");
                return;
            }
            Path temporary = Files.createTempFile(directory, "ingest-", ".tmp");
            try {
                Files.write(temporary, buffer.toByteArray());
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE);
            } finally {
                Files.deleteIfExists(temporary);
            }
        }
    }
}
