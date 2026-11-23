# team-knowledge-api

Build a citation-aware knowledge API for remote engineering teams.

**Status: initial working component; full MVP is planned, not complete.**

Implemented now: Chunk plain text with deterministic overlapping boundaries.

Backend: Java. This seed has no live model calls, credentials, external writes, or deployment.

## Verify

```sh
javac --release 17 -d build src/*.java test/*.java
java -cp build TextChunker
java -cp build SourceFixtures
```

## One-week MVP

See [ROADMAP.md](ROADMAP.md) for seven bounded development days and acceptance criteria. Work is scheduled sequentially across the portfolio, not on every repository every day. The week starts when this project becomes active.

## Evidence

The current automated checks validate the small implemented component only. They do not establish end-to-end AI quality, scalability, or production readiness. Later demos and LinkedIn write-ups should cite measured behavior and disclosed limitations.

SourceChunker attaches document ID, caller-supplied revision and title to every chunk. Citation offsets are zero-based Unicode code-point ranges with an exclusive end. The caller must change the revision when the document changes. Chunking preserves the existing overlap behavior.

DocumentStore.ingest accepts a source and text, persists an immutable revision and supports identical retries. Reopening the same directory restores documents through read(id, revision). IDs and revisions use 1–64 ASCII letters, digits, underscores or hyphens. Each field is limited to 65535 bytes in Java modified UTF-8. The local filesystem must support atomic moves. Use a caller-owned directory; multi-host storage and power-loss durability remain unverified. Run `java -cp build DocumentFixtures` after compilation.

LexicalSearch.search(chunks, query, limit) ranks supplied chunks by the number of distinct query terms present. Terms are Unicode letter/number runs lowercased with Locale.ROOT. Zero-match chunks are omitted, repeated query terms count once, and ties use document ID, revision and chunk index. Results retain full source metadata and offsets. This baseline has no stemming or semantic matching. Callers select the corpus; access filtering remains a later milestone. Run `java -cp build SearchFixtures` after compilation.

VectorSearch accepts an EmbeddingProvider and ranks source chunks by cosine similarity, returning up to the requested limit including zero or negative scores. It rejects empty, zero, nonfinite and mismatched vectors. The provider must use the same embedding space for query and documents. Current validation uses deterministic fixture vectors. Live embeddings and semantic relevance remain unevaluated. Run `java -cp build VectorFixtures` after compilation.
