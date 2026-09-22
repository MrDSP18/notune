# NØTUNE Feature Matrix

Status values: `PASS`, `FAIL`, `BLOCKED`, `PARTIAL`, `UNVERIFIED`, `NOT IMPLEMENTED`.

This matrix records verified implementation status. A screen, model, or placeholder data source is not evidence that a feature works end to end.

| Area | Status | Evidence / next gate |
| --- | --- | --- |
| Dedicated transformation branch | PASS | `feature/notune-platform-rebuild` created from `main`. |
| Basic Android build | UNVERIFIED | Run debug and release build gates before claiming readiness. |
| Application identity migration | NOT IMPLEMENTED | `namespace` and `applicationId` remain `echo.music.iad1tya`; audit integration identifiers before migrating. |
| Provider abstraction and catalog truth | PARTIAL | Existing source integrations remain provider-specific; no verified unified availability model. |
| Playback hardening | PARTIAL | Media3 implementation exists; lifecycle and failure-path validation remains required. |
| Adaptive queue / Flow | PARTIAL | Existing implementation requires data-quality and behavior testing. |
| AI tool execution | PARTIAL | Tool infrastructure exists; all providers and action confirmation need end-to-end validation. |
| Music DNA | PARTIAL | Existing analytics/DNA code requires real-data and insufficient-data validation. |
| Social repository outbox | PARTIAL | Friend/post actions persist locally and queue remote intent. `blockUser` and `unlikePost` are unit-tested. |
| Social UI data integrity | FAIL | Friend Circles, polls, stories, and challenges use hard-coded in-memory data outside the durable social repository. Replace before release. |
| Realtime Rooms | PARTIAL | Existing Listen Together components require event ordering, reconciliation, reconnect, and conflict tests. |
| Private/Couple E2E encryption | UNVERIFIED | Do not claim E2E guarantees without threat model and independent cryptographic review. |
| Voice/video calling | NOT IMPLEMENTED | No verified WebRTC implementation gate recorded. |
| Spotify bridge | PARTIAL | Existing import/integration must be validated against permitted API behavior. |
| Downloads | PARTIAL | Verify permitted sources, offline recovery, storage failures, and corruption handling. |
| Widgets | UNVERIFIED | Require add, resize, update, playback, and restart instrumentation coverage. |
| Accessibility and reduced motion | UNVERIFIED | Require TalkBack, contrast, font scaling, and reduced-motion checks. |
| Security audit | PARTIAL | Credential handling has prior work; source/history/APK/log/component audit remains required. |
| Release validation | NOT IMPLEMENTED | Requires release build, APK install/launch smoke test, device matrix, and CI gates. |

## Immediate Delivery Order

1. Replace hard-coded social UI with repository-backed state and explicit unavailable states.
2. Define provider-neutral catalog, availability, and source-selection contracts.
3. Formalize room event protocol, persistence, and reconciliation tests.
4. Complete identity migration only after Firebase, OAuth, deep links, providers, database, widgets, and release metadata are audited.