# NØTUNE User Data Protection, Privacy and Secure Data-Handling Audit

**Audit date:** 2026-09-13  
**Scope:** Android application and in-repository modules under `Echo-Music`; static source review, configuration review, secret-pattern scan, and FOSS unit tests. No backend source, production infrastructure, provider consoles, device test, packet capture, or second-user authorization environment was available.

## Executive Verdict

# CRITICAL SECURITY ISSUES FOUND

The implementation is local-first in several areas, but the privacy contract is not currently enforceable end to end. The highest-risk issues are plaintext sensitive local storage and backup exposure, globally permitted cleartext networking, unsafe room encryption failure behavior (fixed in this audit), incomplete deletion/retention enforcement, and no verifiable backend authorization or cloud deletion implementation in this repository.

## Findings

### P0 / Critical

1. **Room encryption previously failed open.** `RoomEncryptionManager` returned the original plaintext when encryption or decryption failed. This violated the claimed encrypted-room boundary. The encryption path now throws `SecurityException` instead of returning plaintext, but the room protocol still requires backend/protocol review before an E2E claim is valid.
2. **Room key lifecycle is not a verified group E2E protocol.** An in-memory ECDH key pair is generated locally; signing helpers exist, but this review found no complete authenticated identity/trust workflow. `rotateKey` derives a new key from the old key plus a random salt. That is not sufficient group revocation unless fresh key material is delivered through a trusted channel unavailable to the removed member.
3. **Secrets were found in the checkout environment.** The local untracked `.git/config` contains a credential-bearing GitHub remote URL. It is not tracked by Git and is not reproduced here. Treat it as compromised and rotate/revoke it immediately. Historical commits also contain Google client keys, and source contains hardcoded public web-client keys; public does not mean unrestricted or safe to allowlist.

### P1 / High

1. **Sensitive DataStore values are plaintext.** `settings` uses ordinary Preferences DataStore. It contains YouTube cookies, sync identifiers, Spotify tokens, proxy passwords, and user-supplied AI provider keys. No Android Keystore-backed DataStore encryption wrapper was found.
2. **Room database is plaintext and was backed up.** `song.db` contains playback/history, searches, library/playlists, recognition data, couple data, memories, room metadata, messages, and member records. Room is created without SQLCipher or field encryption. Backup rules previously excluded only ExoPlayer data, not `song.db` or DataStore. This audit excludes both from legacy and Android 12+ backup rules.
3. **Cleartext traffic was globally enabled and is now narrowed.** The base configuration now rejects cleartext and permits it only for `localhost` and `127.0.0.1` to support explicitly local Ollama. Other HTTP endpoints remain a release finding if they are used outside loopback.
4. **Full reset did not delete all local user data.** Before this audit it cleared only listening/search tables, an AI cache, and exports. It did not clear the Room database, DataStore credentials/settings, couple/room tables, downloaded files, image/song caches, or Keystore keys. The reset now clears all Room tables and DataStore preferences, but cloud/server deletion and all external files remain unverified.
5. **Retention preference is not enforced by an identified scheduler.** `DataRetentionPeriodKey` exists, but no verified local/cloud cleanup worker was found for the advertised periods. There is no evidence of server retention, retryable deletion, or offline synchronization deletion semantics.
6. **Cloud/backend access control cannot be verified.** The repository contains client integrations and room/listen-together transports, but no authoritative backend authorization implementation or tests for cross-user, cross-room, playlist, AI, or couple resource access.

### P2 / Medium

1. **AI provider exposure is real and now has a central client gate.** Gemini, Groq, OpenRouter, and configurable Ollama endpoints receive the prompt and optional system instruction. `AiEngine` now denies all providers during Private Session, denies cloud providers when AI personalization is disabled, and sanitizes prompts before dispatch. Provider retention/training settings remain outside this client.
2. **AI private-chat persistence is not demonstrated.** A `PrivateAiChatKey` exists and the AI privacy model describes excluded conversation storage, but no complete persistence/deletion implementation for conversations was found. `DataDeletionManager` only removes `cache/ai_cache`.
3. **Export is local but not confidential at rest.** `DataExporter` writes an unencrypted ZIP under the app cache. It contains favorites, playlists, top history, preferences, Music DNA, FLOW, room, and couple sections. The current implementation emits placeholder or incomplete values for some sections and does not prove that all database records are exported. File sharing lifecycle and export authentication were not tested.
4. **Logging contains potentially sensitive metadata.** Production code has many `Timber` calls that include search queries, song/artist names, IDs, response/error details, export paths, and state changes. Debug-tree installation is gated, but crash handling and release logging configuration need a device/release verification. No token redaction policy was found across all clients.
5. **Replay protection is process-local.** Replay state is held in memory and can be lost on process death. It is not a server-verifiable message acceptance rule. Sequence handling is not demonstrated for multiple senders, epochs, persistence, or resynchronization.
6. **Sensitive Android permissions are declared.** Location, microphone, external/media storage, Bluetooth, foreground services, notifications, and package installation permissions are present. This review did not prove that every declared permission is needed in the release flows; they should be justified and tested per flavor.

### P3 / Low / Hardening

1. No evidence of `FLAG_SECURE` configuration for history, rooms, couple, or private AI screens was found. Screenshots and recording cannot be claimed impossible without it; Android/device limitations still apply.
2. Notification privacy was not fully verified. The review found notification infrastructure but no complete redaction test for private messages, couple content, AI content, playlists, or account data.
3. Dependency vulnerability scanning was not completed. Gradle dependencies were inventoried, but no authenticated advisory database or reproducible dependency audit was available in this run.

## Data Inventory and Flow Matrix

| Data | Source | Local storage | Cloud / network | AI | Third party | Encryption | Retention | Delete |
|---|---|---|---|---|---|---|---|---|
| Account/session identity | YouTube login/account import; `AccountData` | DataStore cookie, visitor data, sync ID, account fields; plaintext | YouTube/InnerTube; sync and playlist operations | Not inherently | Google/YouTube endpoints | TLS when HTTPS; local values not encrypted by app | No verified schedule | Logout removes some YouTube values; full reset now clears DataStore; remote revocation/deletion unverified |
| Listening history | Playback/event writers and recognition | Room `Event`, `PlayCount`, recognition/search tables; plaintext | YouTube/other service requests can contain song/query context; no complete sync map | Not automatically proven; local Music DNA reads history | YouTube and lyric/recognition providers receive requested metadata | DB plaintext; HTTPS for HTTPS endpoints | No verified retention worker | History action clears selected history; full reset clears Room; private-session coverage requires integration tests |
| Music DNA / personalization | Local history analysis and memory manager | Derived locally; database/source history | No verified dedicated upload; cloud personalization toggle enforcement incomplete | Provider can receive whatever caller puts in prompt | Configured AI provider if included by caller | Local DB/plain values | No verified schedule | No dedicated derived-data deletion contract; full DB reset removes local state |
| Favorites / playlists / queue | User library and player | Room tables and persistent queue/preferences; plaintext | YouTube/InnerTube and optional sync/Drive-related modules | Only if caller includes them | YouTube/Google Drive integration in GMS | TLS for HTTPS; local plaintext | No verified schedule | Full Room reset local; remote playlist deletion/sync behavior unverified |
| AI prompts/responses | User prompt and current-track context | No complete conversation store found; cache directory may exist | Gemini, Groq, OpenRouter, configurable Ollama | Cloud AI for first three; Ollama is local only when endpoint is truly local | Provider retains data under its own contract | HTTPS for cloud endpoints; API keys in plaintext DataStore | Provider retention unknown; client retention incomplete | Cache deletion only; no verified provider deletion or private-chat persistence boundary |
| Rooms/messages/members | Room/listen-together features | Room tables; message content/metadata plaintext unless separately encrypted before insert | WebSocket/listen-together, signaling, STUN/TURN, server config | No evidence private room data is automatically sent to AI; call-site audit required | Google STUN; configured signaling/TURN services | AES-GCM/ECDH code exists; protocol trust, rotation, and fallback were not fully valid; fallback fixed to fail closed | No verified server retention | Room local rows not covered by old reset; full Room reset now removes local rows; server deletion unverified |
| Couple/memories/our songs | Couple UI and DAOs | Room tables; plaintext entities, aliases, dates, memories, reactions | Transport/server behavior not verifiable from this client alone | Policy says excluded, but universal enforcement unverified | Signaling/TURN or configured services | Same room protocol limitations | No verified schedule | DAO has per-record deletion; full Room reset local; partner/server copies unverified |
| Tokens and provider credentials | OAuth/login/settings | DataStore: YouTube/Spotify/API/proxy credentials; plaintext | Sent to intended provider; tokens may authorize account actions | Prompt sanitizer is not a substitute for transport/storage controls | YouTube, Spotify, AI providers, Last.fm, Google services | TLS where HTTPS; no app-level local encryption | Expiration/rotation varies; no single lifecycle | Some logout paths remove YouTube values; full DataStore reset now clears local values; provider revocation unverified |
| Device/analytics/crash metadata | Android/runtime; GMS SDK | SDK-managed | Firebase Analytics/Crashlytics dependencies in GMS flavor; explicit event scope not fully found | No evidence AI text is intentionally sent, but breadcrumbs/logs need review | Firebase/Google | SDK transport assumed; configuration not independently verified | Firebase policy/console unknown | Analytics toggle enforcement and deletion not demonstrated |

## Local Storage Audit

- **Room:** `song.db`, WAL/journal behavior enabled, broad user-data schema, no database encryption. Access is app-process restricted by Android sandbox, but backup and rooted/debug/device-access threats remain.
- **DataStore:** ordinary Preferences DataStore named `settings`; privacy flags remain plaintext by design, while known credential keys are migrated at application startup into AES-GCM ciphertext protected by an Android Keystore key and their legacy values are removed. The settings UI still writes some credential keys through the generic preference helper, so a complete fix requires routing those write paths through the secure store too.
- **Cache/files:** AI cache and export cache are local; image, audio, downloads, and provider caches exist across modules. Full reset does not prove deletion of all of them.
- **Keystore:** room master AES key uses Android Keystore. It is device-local and not an E2E group identity. The ECDH local key pair is generated in memory by the standard EC provider, not shown as a persistent Keystore identity.
- **Backup:** sensitive `song.db` and `datastore` are excluded in both backup rule formats. Existing backups made before installation/update cannot be retroactively erased by this app.

## Network / TLS / Third Parties

Observed or configured destinations include YouTube/InnerTube and Google video services, Last.fm, lyric providers, Gemini, Groq, OpenRouter, Ollama-configured endpoints, listen-together WebSocket servers, GitHub-hosted server configuration, Google STUN, TURN/signaling infrastructure, and Firebase/Google services in GMS builds. HTTPS/WSS is used for many production defaults and the network config trusts system CAs without a custom trust-all implementation found. The global cleartext allowance remains a release risk.

No GraphQL endpoint was identified in the reviewed client surfaces. No Sentry, Amplitude, Mixpanel, PostHog, or advertising SDK was identified in the reviewed dependency/source search. Firebase Analytics and Crashlytics are flavor-dependent, not present in the FOSS dependency set.

## Privacy Controls

| Setting | UI | Storage | Enforcement evidence | Tested |
|---|---|---|---|---|
| Private Session | Privacy Center | DataStore boolean and `PrivateSessionManager` | Fixed setter now persists state; memory generation checks it; all history writers not proven guarded | No dedicated regression test |
| Listening History | Privacy Center/action | Room | Selected deletion exists; retention/private-session coverage incomplete | No |
| Music DNA | Privacy Center/model | Derived from local history | Local computation exists; toggle-wide enforcement not proven | No |
| AI Personalization | Privacy Center | DataStore boolean | No complete provider call-site enforcement found | No |
| Private AI Chat | Privacy Center | DataStore boolean | Policy metadata exists; persistence/provider retention boundary not proven | No |
| Cloud Sync | Key exists/UI surface | DataStore boolean | Backend/sync enforcement not verified | No |
| Analytics | Key exists and GMS SDK | DataStore boolean | No verified runtime SDK collection gate | No |
| Retention period | Key exists | DataStore string | No verified cleanup scheduler | No |

## Deletion and Resurrection

Local full reset now clears all Room tables, all DataStore preferences, cache children, app files, no-backup files, AI cache, and export cache. It does not prove deletion of Android backups already made, provider-side data, server-side room/listen-together data, Firebase records, YouTube/Spotify/Last.fm records, or data held by AI providers. There is no backend account-deletion implementation in this repository, so the requested delete-account and post-reconnect resurrection test cannot be completed. A real acceptance test must delete server state, sign out, restart, reconnect, synchronize, and assert that no local or remote record returns.

## E2E / Social Security Assessment

AES-GCM with 128-bit tags, randomized 12-byte IVs, ECDH P-256, HKDF-SHA-256, and replay helper code are present. Those primitives are reasonable in isolation. They do not establish E2E security without authenticated identities, trusted key binding, server handling that preserves ciphertext, fresh group epochs on membership change, revocation, persistent/reliable replay state, and tests against a malicious member. Those properties were not verified. The earlier plaintext fallback is fixed, but the protocol should not be marketed as verified E2E yet.

## What Is Protected

- Android app sandbox protects ordinary local files from other non-privileged apps.
- HTTPS/WSS is used by many default cloud transports with platform certificate validation.
- Room message code has AES-GCM/ECDH/HKDF primitives and replay helpers.
- Music DNA computation and much playback/library storage are local-first.
- FOSS builds exclude Firebase dependencies.
- Private-session state persistence, full local reset coverage, backup exclusions, and fail-closed room crypto were improved in this audit.

## What Is Not Protected or Cannot Be Verified

- DataStore credentials and Room user data are not encrypted at rest by the app.
- Cleartext traffic is globally allowed.
- Backend authorization, account deletion, cloud retention, provider deletion, and remote resurrection behavior are unavailable for verification.
- AI provider retention/training controls are not enforced by client code.
- Room identity trust, group-key rotation, revoked-device rejection, and replay behavior across restarts are not proven.
- Export confidentiality, notification redaction, screen-capture behavior, release crash metadata, and all production log paths require device/release testing.
- Dependency CVE status requires a dependency scanner and current advisory data.

## Required Follow-up

1. Revoke the credential embedded in the local Git remote immediately; inspect and rotate any credentials in CI or other clones.
2. Replace plaintext credential storage with a Keystore-backed design, or use a maintained encrypted storage implementation with migration and key invalidation behavior.
3. Encrypt the Room database or separate and minimize highly sensitive fields; keep backup exclusions as defense in depth.
4. Disable base cleartext traffic and explicitly constrain any local-only HTTP exception.
5. Define and implement server-side authentication, authorization, retention, deletion, device revocation, and export endpoints with cross-user tests.
6. Replace the room protocol with an authenticated, epoch-based group protocol using fresh post-revocation secrets; add malicious-member and replay tests.
7. Gate every AI call through one policy-enforcing boundary, add provider-specific privacy modes, and document provider retention.
8. Add regression tests for private session, deletion/resurrection, backup rules, cross-user rejection, revoked devices, replay after restart, notification redaction, and token/log redaction.

## Final Checklist

- [x] Data inventory and local data-flow mapping completed
- [x] Local Room/DataStore/cache audit completed
- [x] Keystore and primitive review completed
- [x] Token and network source review completed
- [x] AI provider surface reviewed
- [x] Logging/crash/analytics dependency review completed
- [x] Backup rules reviewed and hardened
- [x] Secret-pattern scan completed without reproducing secrets
- [x] Focused FOSS unit tests passed after using a workspace-local temporary directory
- [ ] Backend authorization tested
- [ ] Cloud retention/deletion verified
- [ ] Delete-account resurrection test executed
- [ ] Device revocation and group-key rotation verified
- [ ] Notification/screenshot release tests executed
- [ ] Current dependency vulnerability scan completed

**Status: CRITICAL SECURITY ISSUES FOUND**
