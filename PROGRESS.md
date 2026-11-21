# Progress

## 20 November — source-aware chunks

Day 1 complete. Source metadata follows every overlapping chunk. Fixtures verify exact Unicode offsets, revision separation, invalid metadata and empty text. Java 17 compilation and seed checks pass. Document ingestion and persistence are next. [Java checks](https://github.com/akshay-dhangar/team-knowledge-api/actions/workflows/ci.yml)

## 21 November — document storage

Day 2 adds local ingestion through the Java library and immutable persisted revisions. Fixtures cover Unicode restart round trips, retries, conflicting content preservation and path rejection. Next is lexical retrieval. [Java checks](https://github.com/akshay-dhangar/team-knowledge-api/actions/workflows/ci.yml)
