# NØTUNE Architecture Reference — Production Unified Architecture

**Version**: 3.0  
**Platform**: Android (Media3 ExoPlayer, Jetpack Compose, Kotlin Coroutines, Hilt, Room, DataStore)  
**Status**: Production Unified Target Architecture  

---

## High-Level System Diagram

```text
                                  NØTUNE UI LAYER
  ┌──────────┬──────────┬───────────┬──────────┬─────────┬───────────┬──────────┐
  │   Home   │  Search  │ AskNØTUNE │  Player  │  Queue  │  Library  │  Lyrics  │
  ├──────────┼──────────┼───────────┼──────────┼─────────┼───────────┼──────────┤
  │ MusicDNA │  Social  │   Rooms   │ Settings │Downloads│Recognition│ Audiolab │
  └──────────┴──────────┴───────────┴──────────┴─────────┴───────────┴──────────┘
                                        │
                                        ▼
                          NØTUNE APPLICATION COORDINATOR
                                        │
             ┌──────────────────────────┴──────────────────────────┐
             ▼                                                     ▼
┌───────────────────────────┐                         ┌───────────────────────────┐
│  NØTUNE INTELLIGENCE 3.0  │                         │   UNIFIED MUSIC CATALOG   │
│                           │                         │                           │
│ • IntentEngine            │                         │ • ProviderRegistry        │
│ • UnifiedContextEngine    │                         │ • LocalMediaStoreProvider │
│ • UserMemoryEngine        │                         │ • YouTubeInnerTubeProvider│
│ • Music Brain             │                         │ • CatalogSearchResolver   │
│ • CandidateEngine         │                         │ • Offline Downloads       │
│ • CentralizedRankingEngine│                         └─────────────┬─────────────┘
│ • DiversityController     │                                       │
│ • AdaptiveQueueOrchestrator                                       │
│ • FeedbackEngine          │                                       │
└────────────┬──────────────┘                                       │
             │                                                      │
             ▼                                                      ▼
┌───────────────────────────┐                         ┌───────────────────────────┐
│     AI TOOL EXECUTOR      │                         │     PLAYBACK ENGINE       │
│                           │                         │                           │
│ • NotuneToolRegistry (29) │                         │ • CatalogResolver         │
│ • AiToolExecutor          │────────────────────────►│ • ResolvedPlaybackSource  │
│ • NoTuneBasicAiProvider   │                         │ • PlayerConnection        │
│ • External LLMs (Optional)│                         │ • MusicService / Media3   │
└───────────────────────────┘                         └─────────────┬─────────────┘
                                                                    │
                                                                    ▼
                                                       ┌───────────────────────────┐
                                                       │  MEDIA3 PLAYBACK FEEDBACK │
                                                       │                           │
                                                       │ • Media3PlaybackListener  │
                                                       │ • 30 UserEvent Telemetry  │
                                                       │ • RewardCalculator        │
                                                       └───────────────────────────┘
```

---

## Core Subsystem Architectures

### 1. Presentation & UI Layer
- **Framework**: Jetpack Compose, Material 3, Nothing OS Industrial Design System.
- **Tokens & Theming**: 10 dynamic color themes (`Tokyo Neon`, `Midnight`, `Aurora`, `Synthwave`, etc.), 10 dynamic app logos, NothingFont dot-matrix typography.
- **Adaptive Layouts**: Responsive support across `COMPACT`, `MEDIUM`, and `EXPANDED` screen sizes.
- **Core Screens**: Home, Search, Ask NØTUNE, Player, Queue, Library, Lyrics, Music DNA, Social Hub, Listen Together Rooms, Settings, Downloads, Audio Lab, Music Recognition.

### 2. NØTUNE Intelligence 3.0 Layer
- **IntentEngine**: Offline NLP rule parser mapping queries into 12 structured intents.
- **UnifiedContextEngine**: Captures 5-layer context snapshot (playback state, session taste, battery %, network, room state).
- **UserMemoryEngine**: Stores persistent decaying memories across EXPLICIT, BEHAVIORAL, SESSION, and MUSIC categories.
- **Music Brain**: Deterministic 12-D feature vector scorer evaluating energy, valence, tempo, acousticness, artist affinity, language preference, and novelty.
- **CandidateEngine**: Fetches multi-source candidate pools from `ProviderRegistry` and deduplicates using `MusicIdentityEngine`.
- **CentralizedRankingEngine**: 5-signal candidate scorer (TasteMatch 40%, ContextMatch 25%, FlowTransition 20%, Novelty 10%, ProviderQuality 5%).
- **DiversityController**: Filters out artist over-exposure based on discovery tolerance.
- **AdaptiveQueueOrchestrator**: Manages a 5-zone queue (Now Playing, Next 3 High Confidence, Balanced 5, Discovery 5, Candidate Pool) while protecting user-locked items.
- **FeedbackEngine**: Ingests 30 `UserEvent` telemetry types, calculates scalar rewards (-1.0 to +1.0), and updates persistent user taste.

### 3. Unified Music Catalog Layer
- **ProviderRegistry**: Aggregates `LocalMediaStoreProvider` (device storage) and `YouTubeInnerTubeProvider` (online streaming API).
- **TrackRights & Availability**: Checks `isStreamable`, `isDownloadable`, and exclusion constraints before queueing.
- **MusicIdentityEngine**: Normalizes track titles and artist names to deduplicate cross-provider items.

### 4. Playback Engine Layer
- **Media3 / ExoPlayer**: Single authoritative background playback service (`MusicService`) controlled via `PlayerConnection`.
- **Audio Quality & Streaming**: OPUS/AAC stream resolution with gapless playback, crossfade, and automix support.
- **Media3PlaybackListener**: Streams real-time playback events (`Play`, `Complete`, `Skip`, `Replay`, `Favorite`) to `FeedbackEngine`.

### 5. Social & Listen Together Rooms Layer
- **ListenTogetherManager**: Authoritative room state machine managing room creation, joining via 6-character code, presence, and playback sync.
- **SocialRepository**: Offline-first social outbox (`PendingSocialAction`) managing profiles, friends, messages, and music sharing.
- **Canonical Deep Links**: `/song/{id}`, `/album/{id}`, `/artist/{id}`, `/playlist/{id}`, `/room/{id}`, `/user/{id}`.

### 6. Security & Privacy Layer
- **Credentials**: Zero plain-text tokens or API keys committed to source. API keys stored encrypted in `SecureStorageManager` / Android DataStore.
- **Privacy Controls**: Incognito session mode, pause/resume learning, forget memory items, and complete personalization reset.
