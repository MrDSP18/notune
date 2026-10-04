# NØTUNE Adaptive Flow Architecture

## Overview

The **NØTUNE Adaptive Flow Engine** dynamically transforms user song/album/playlist selections into live, evolving session seeds. Rather than generating a static queue or replacing the currently playing track, the Flow Engine continuously orchestrates upcoming tracks based on real-time listening behavior, skip velocity, transition harmony, and user DNA.

---

## Authoritative Pipeline

```text
USER SELECTION (Track / Album / Playlist / Artist)
       │
       ▼
SessionSeedManager (Builds SessionSeed & SessionProfile)
       │
       ▼
UnifiedContextEngine (Gathers live device/network/time state)
       │
       ▼
CandidateEngine & CatalogSearchResolver
       │
       ▼
AvailabilityFilter & ExclusionEngine (Removes duplicates & blocked tracks)
       │
       ▼
CentralizedRankingEngine & TransitionEngine (Musical distance & flow scoring)
       │
       ▼
AdaptiveQueueEngine (Queue Tiers: NOW / NEXT_3 / BALANCED_5 / DISCOVERY_5 / RESERVE_POOL)
       │
       ▼
AdaptiveQueueOrchestrator $\rightarrow$ Media3 PlayerConnection
       │
       ▼
Playback & Continuous Feedback Loop (Weighted Skip / Replay / Favorite)
```

---

## Core Components

1. **`SessionSeedManager`**:
   - Captures source type (`TRACK`, `ALBUM`, `PLAYLIST`, `ARTIST`, `LOCAL_TRACK`, `ROOM_QUEUE`, `SEARCH_RESULT`).
   - For authored content (`PLAYLIST`, `ALBUM`), preserves author sequence before enabling adaptive continuation.

2. **`TransitionEngine`**:
   - Calculates musical distance `distance(A, B)` using energy, tempo/BPM, valence, genre, language, and artist affinity.
   - Classifies transitions: `SMOOTH`, `EVOLVING`, `DISCOVERY`, `MOOD_SHIFT`.
   - Guarantees energy continuity (e.g. $82 \rightarrow 76 \rightarrow 84 \rightarrow 79$).

3. **`AdaptiveQueueEngine`**:
   - Weighted feedback model:
     - `EARLY_SKIP` (<10s): -0.8
     - `SKIP` (10-30%): -0.5
     - `LATE_SKIP` (70-90%): -0.2
     - `COMPLETE`: +0.4
     - `REPLAY`: +0.7
     - `FAVORITE`: +1.0
   - Feature-level feedback adaptation adjusts session energy/tempo parameters rather than blanket artist bans.
   - Current track (`NOW`) remains strictly immutable while audio plays.

4. **`AdaptiveFlowBar`**:
   - Compose control with `FAMILIAR` (80/20) $\longleftrightarrow$ `DISCOVERY` (25/75) ratio slider and expandable parameter customization.
