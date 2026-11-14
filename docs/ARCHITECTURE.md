# NØTUNE — System Architecture & Component Design

## 1. System Overview

NØTUNE is structured according to Clean Architecture and Modern Android Development (MAD) principles. The architecture isolates user interface concerns, media playback services, audio DSP processing, offline persistence, and external service communication into loosely coupled modules.

```mermaid
graph TD
    subgraph Presentation_Layer [UI Layer (:app)]
        MainActivity[MainActivity]
        Navigation[Jetpack Compose Navigation]
        PlayerUI[Player Screens / Sheets / MiniPlayer]
        LibraryUI[Library / Playlists / Artists / Albums]
        AskNoTuneUI[Ask NØTUNE AI Chat Interface]
    end

    subgraph Service_DSP_Layer [Playback & DSP Engine (:playback)]
        MusicService[MusicService MediaLibraryService]
        PlayerConn[PlayerConnection Controller]
        ExoPlayerCore[AndroidX Media3 ExoPlayer]
        AudioProcessors[CustomEqualizer / Automix / StereoWidener / SilenceDetector]
        BiquadEngine[BiquadFilter DSP Engine]
    end

    subgraph Domain_Data_Layer [Core & Data Layer (:core)]
        MusicDB[(Room Database: song.db)]
        DataStorePrefs[DataStore Preferences]
        SecureStorage[Android KeyStore SecureStorageManager]
        EventRepo[EventRepository & AppEvents]
        LocalMediaRepo[LocalMediaRepository]
    end

    subgraph Intelligence_Sync [Intelligence & Sync (:app & :innertube)]
        AiEngine[AiEngine Multi-Provider Orchestrator]
        AIPrivacy[AIPrivacyGateway & AIRequestSanitizer]
        AiToolMgr[AiToolManager Tool Executor]
        CloudSync[CloudSyncManager & CloudSyncWorker]
        InnerTubeClient[InnerTube YouTube Music Client]
    end

    MainActivity --> Navigation
    Navigation --> PlayerUI
    Navigation --> LibraryUI
    Navigation --> AskNoTuneUI

    PlayerUI --> PlayerConn
    PlayerConn --> MusicService
    MusicService --> ExoPlayerCore
    ExoPlayerCore --> AudioProcessors
    AudioProcessors --> BiquadEngine

    LibraryUI --> LocalMediaRepo
    LocalMediaRepo --> MusicDB

    AskNoTuneUI --> AiEngine
    AiEngine --> AIPrivacy
    AiEngine --> AiToolMgr
    AiToolMgr --> PlayerConn
    AiToolMgr --> InnerTubeClient

    EventRepo --> CloudSync
    CloudSync --> SecureStorage
```

---

## 2. Layer Descriptions

### 1. Presentation Layer (`:app`)
- **Technology**: Jetpack Compose, Material 3, ViewModel, Kotlin Coroutines & StateFlow.
- **Components**: Reactive screens with unidirectional data flow (UDF). All states are represented as immutable `StateFlow` primitives.
- **Crash Prevention**: Guarded with `safeMetadata` extensions; unmanaged `CoroutineScope` invocations inside composables replaced with structured `LaunchedEffect` and `withContext(Dispatchers.IO)`.

### 2. Playback & DSP Engine (`:playback`)
- **Technology**: AndroidX Media3 (`MediaLibraryService`), ExoPlayer, PCM AudioProcessors.
- **Audio Pipeline**:
  ```
  AudioDecoder -> CustomEqualizer -> AutomixDuck -> StereoWidener -> SilenceDetector -> AudioTrack
  ```
- **Stability**: Every audio processor validates incoming audio format (`channelCount in 1..2`, `sampleRate > 0`) prior to buffer allocation, preventing arithmetic panics and audio glitches.
- **Sleep Timer**: Executes gradual volume ramp-down using `try ... finally` semantics to prevent lingering muted states.

### 3. Core Data & Persistence (`:core`)
- **Room Database (`song.db`)**: SQLite with Write-Ahead Logging (`WAL`), synchronous mode `NORMAL`, and dedicated 4-thread executors for read/write isolation.
- **Migrations**: Strict versioned migrations (`MIGRATION_1_2` through `48_49`) without destructive wiping.
- **Security**: Hardware KeyStore (`AndroidKeyStore`) AES-256-GCM authenticated encryption for sensitive tokens (`SecureCredentialStore`, `SecureStorageManager`).

### 4. Intelligence & Background Sync (`:app`)
- **AI Tool Matrix**: `AiEngine` operates a multi-step execution loop connecting reasoning LLMs (Groq, Gemini, OpenRouter, Ollama, Local) with real Android tool actions (`search_songs`, `play_song`, `create_playlist`, `pause_music`, etc.).
- **Privacy Enforcement**: `AIPrivacyGateway` and `AIRequestSanitizer` intercept and sanitize every AI request before external transmission.
- **Cloud Sync**: Offline-first persistent disk outbox (`notune_sync_outbox.json`) driven by AndroidX `WorkManager` with exponential backoff and idempotency keys.
