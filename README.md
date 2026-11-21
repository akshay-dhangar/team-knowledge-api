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
