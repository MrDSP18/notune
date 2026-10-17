# NØTUNE v3.2.0 — Adaptive Music Intelligence & Unified NØ AI Architecture

## 1. Physical Device Acceptance Test Protocol

Perform the following manual verification battery on two physical Android test devices running `app-universal-foss-release.apk`:

### 📱 Acceptance Verification Battery
1. **First Launch & Onboarding**:
   - Clean install -> Launch -> Request permissions (Media / Storage / Notifications) -> Verify zero runtime crash.
   - Local storage scan locates local `.mp3`/`.flac`/`.m4a` audio files.
2. **Offline Local Playback**:
   - Turn WiFi & Mobile Data **OFF**.
   - Play, pause, seek, previous/next track.
   - Verify lock screen controls and system notification player widgets.
   - Verify Bluetooth headset play/pause and media key responsiveness.
3. **Adaptive Queue & Lyrics**:
   - Enable network connection. Start playback.
   - Verify lyrics load synchronized with audio timestamp.
   - Enable translation view toggle; verify no playback stutter or UI freeze.
   - Turn network **OFF** during playback; verify graceful lyrics fallback without breaking audio stream.
4. **Listen Together (Rooms)**:
   - Device A creates a Room; Device B joins via 6-character room code or deep-link `/r/:roomId`.
   - Play/pause on Device A updates Device B state within <200ms.
   - Disconnect Device B network; verify auto-reconnection upon network restore.
5. **Universal App Links & Web Fallback**:
   - Tap shared link `/s/:songId` or `/p/:playlistId`:
     - **App Installed**: Opens NØTUNE native player directly via `notune://open/...`.
     - **App Absent**: Opens NØTUNE Web Player (`https://notune-web.pages.dev`) with instant audio preview.

---

## 2. Unified NØ AI Engine Recommendation Contract

Currently, NØ AI operates through two distinct implementations:
- **Android**: Native LiteRT-LM (Gemma-2B / Qwen) running on-device.
- **Web Platform**: Local acoustic vector matrix solver computing cosine similarity.
- **Edge Backend**: Deterministic D1 SQL similarity recommendation API (`/api/v1/recommendations/adaptive`).

### 🎯 Standardized NØ AI Output Interface
To ensure platform parity and ease of evolution, all three engines will conform to a unified contract:

```typescript
interface RecommendationRequest {
  currentTrackId: string;
  history: String[];
  userSessionMood: {
    valence: number;    // 0.0 - 1.0 (Sad -> Happy)
    energy: number;     // 0.0 - 1.0 (Calm -> Energetic)
    danceability: number;
    genreVector: Record<string, number>;
  };
  behavioralSignals: {
    skipsInRow: number;
    replays: number;
    avgCompletionRate: number;
  };
  limit: number;
}

interface RecommendationResponse {
  engine: "litert-lm" | "acoustic-vector-web" | "edge-d1-api";
  recommendations: TrackRecommendation[];
}

interface TrackRecommendation {
  trackId: string;
  title: string;
  artist: string;
  score: number;        // Normalized 0.0 - 1.0 confidence
  reason: string;       // e.g. "Similar acoustic valence & high energy transition"
  mood: string;         // e.g. "Focus", "Chill", "Upbeat"
  similarity: number;   // Cosine similarity
}
```

---

## 3. NØTUNE v3.2.0 Adaptive Queue Pipeline

```
┌─────────────────────────┐
│ User Interaction Signal │
│ (Skip, Like, Replay)    │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐      ┌─────────────────────────┐
│ Dynamic Context Solver  ├─────►│ Candidate Generation    │
│ (Valence & Energy)      │      │ (Pool of 50 tracks)     │
└─────────────────────────┘      └────────────┬────────────┘
                                              │
                                              ▼
┌─────────────────────────┐      ┌─────────────────────────┐
│ Queue Injection         │◄─────┤ Diversity & Fatigue     │
│ (Top 5 Next Songs)      │      │ Filter (No consecutive) │
└─────────────────────────┘      └─────────────────────────┘
```

1. **Signal Aggregation**:
   - **Skip (<15s)**: Decreases current mood cluster weight by 0.15.
   - **Replay / Complete (>90%)**: Increases acoustic similarity weight for current genre & BPM by 0.2.
   - **Like / Favorite**: Seeds long-term Music DNA vector.
2. **Candidate Generation**:
   - Queries local vector cache & Edge API for candidate tracks matching current session momentum.
3. **Diversity & Anti-Fatigue Filtering**:
   - Prevents playing more than 2 consecutive tracks from the same artist.
   - Enforces smooth BPM transitions (max 15% delta between adjacent tracks).
