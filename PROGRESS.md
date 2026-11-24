# Progress

## 20 November — source-aware chunks

Day 1 complete. Source metadata follows every overlapping chunk. Fixtures verify exact Unicode offsets, revision separation, invalid metadata and empty text. Java 17 compilation and seed checks pass. Document ingestion and persistence are next. [Java checks](https://github.com/akshay-dhangar/team-knowledge-api/actions/workflows/ci.yml)

## 21 November — document storage

Day 2 adds local ingestion through the Java library and immutable persisted revisions. Fixtures cover Unicode restart round trips, retries, conflicting content preservation and path rejection. Next is lexical retrieval. [Java checks](https://github.com/akshay-dhangar/team-knowledge-api/actions/workflows/ci.yml)

## 22 November — lexical baseline

Day 3 adds deterministic distinct-term retrieval over source chunks. Fixtures verify ranking, duplicate query terms, whole-token matching, empty queries, deterministic ties and source preservation. Java checks pass locally. Next is pluggable embeddings and vector retrieval. [Java checks](https://github.com/akshay-dhangar/team-knowledge-api/actions/workflows/ci.yml)

## 23 November — vector retrieval

Day 4 adds pluggable embeddings and cosine ranking with preserved source chunks. Fixtures cover known rankings, very large finite coordinates, invalid vectors and dimension mismatch. Next is cited answers through an API. [Java checks](https://github.com/akshay-dhangar/team-knowledge-api/actions/workflows/ci.yml)

## 23 November — cited answer endpoint

Day 5 provides a loopback HTTP endpoint over selected stored documents. Real HTTP fixtures verify exact passage citations, no-match output and oversized-query rejection. Answers are lexical extracts. Access-boundary and retrieval-quality evaluation are next. [Java checks](https://github.com/akshay-dhangar/team-knowledge-api/actions/workflows/ci.yml)
