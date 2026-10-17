# NØTUNE v3.2.1 — Focused Reliability & Polish Backlog

**Target Release**: NØTUNE v3.2.1  
**Baseline**: NØTUNE v3.2.0 (`develop/notune-v3.2`)  
**Status**: DRAFT / READY FOR BRANCH CREATION  

---

## Priority Classification Scheme

- **P0**: Release Blockers — Crashes, playback failures, security vulnerabilities, data corruption, broken backend.
- **P1**: Reliability & Core Quality — Incorrect metadata, lyrics failures, adaptive queue regressions, room sync drops.
- **P2**: UX & Performance Polish — Navigation smoothness, artwork rendering, loading skeletons, layout adjustments.
- **P3**: Enhancements & Future Features — Additional metadata providers, offline vector indexing, advanced social tools.

---

## Backlog Items

### P0 — Release Blockers
* **None currently identified.** (v3.2.0 baseline has 0 active P0 issues across 294/294 passing tests, verified playback immunity, and security audit).

---

### P1 — Reliability & Quality Improvements

| ID | Title | Component | Description |
| :--- | :--- | :--- | :--- |
| **REL-01** | Multi-Language Soundtrack Resolution Tuning | Metadata Resolver | Fine-tune entity resolution confidence thresholds for Indian movie soundtracks released under identical album titles across multiple regional languages (e.g. Tamil / Telugu / Hindi album releases). |
| **REL-02** | LRCLIB Timestamp Formatting Edge Cases | Lyrics Module | Add lenient regex parser for non-standard millisecond timestamp formats in community-submitted LRCLIB synced lyrics (e.g., `[01:23:4]` vs `[01:23.45]`). |
| **REL-03** | Room Reconnect Jitter Mitigation | Social / Rooms | Implement exponential backoff with random jitter during rapid network interface switches (Wi-Fi <-> 5G/4G) to prevent room WebSocket reconnect storms. |
| **REL-04** | Cache Eviction Grace Period | Local Cache | Extend local metadata cache TTL grace period during extended offline periods to preserve knowledge graph views when offline for >7 days. |

---

### P2 — UX & Performance Polish

| ID | Title | Component | Description |
| :--- | :--- | :--- | :--- |
| **UX-01** | Knowledge Graph Skeleton Shimmers | UI / Deep Intelligence | Add sleek Compose shimmer animations while Deep Music Intelligence enrichment resolves asynchronously in the background. |
| **UX-02** | Artist & Album Navigation Transitions | UI / Details | Polish shared-element transitions when tapping artist / composer / movie links inside the song details view. |
| **UX-03** | Local Storage Settings Diagnostics | UI / Settings | Display broken down cache usage statistics (metadata cache size, lyrics cache size, artwork thumbnail cache size) in settings. |

---

### P3 — Enhancements & Future Explorations

| ID | Title | Component | Description |
| :--- | :--- | :--- | :--- |
| **ENH-01** | Offline Recommendation Indexing | Adaptive Engine | Pre-compute lightweight local vector embeddings for offline adaptive queue generation when disconnected from network. |
| **ENH-02** | Secondary Metadata Provider Fallback | Backend Worker | Explore integration of secondary open metadata APIs as secondary fallbacks for rare indie tracks. |
