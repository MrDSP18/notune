# GOOGLE STITCH TO NØTUNE ANDROID UI MAPPING

**Google Stitch Project**: `https://stitch.withgoogle.com/projects/17889210348887420664`  
**Local Repository Reference**: `/home/dharan-25486/Documents/music/V2/notune/stitch_n_tune_music_os`  

---

## 1. SCREEN MAPPING (STITCH → NØTUNE)

```text
GOOGLE STITCH SCREEN                NØTUNE ANDROID IMPLEMENTATION
─────────────────────────────────────────────────────────────────────────────
Home Dashboard                       com/music/echo/ui/screens/HomeScreen.kt
AI DJ Intelligence                   com/music/echo/social/ui/AskNoTuneScreen.kt
Listen Together Room Sync            com/music/echo/ui/screens/ListenTogetherScreen.kt
Library Offline Storage Matrix       com/music/echo/ui/screens/LibraryScreen.kt
Public Discovery                     com/music/echo/notune/rooms/ui/PublicDiscoveryScreen.kt
Player Container & Waveform          com/music/echo/ui/player/AdaptivePlayerContainer.kt
Custom Lab Theme Engine              com/music/echo/ui/theme/ThemeEngine.kt
```

---

## 2. COMPONENT MAPPING (STITCH → NØTUNE)

```text
STITCH REFERENCE COMPONENT           NØTUNE JETPACK COMPOSE COMPONENT
─────────────────────────────────────────────────────────────────────────────
SYS.TIME / Telemetry Header         SystemTelemetryHeader (NoTuneTelemetryComponents.kt)
Biosensory Vibe Radar               VibeRadarCard (NoTuneTelemetryComponents.kt)
Audio Format Indicators (DSD/FLAC)  AudioResolutionBadge (NoTuneTelemetryComponents.kt)
Command Terminal Panel              AskNoTuneScreen AI Terminal Panel
Low-Latency Room Telemetry Bar      ListenTogetherScreen Room Sync Status Bar
Hairline Border Container           Card with 1dp border (Color: 0xFF27272A)
OLED Pitch Black Surface            Color: 0xFF131313 & 0xFF09090B
Vivid Crimson Accent                Color: 0xFFFF002E
```

---

## 3. TYPOGRAPHY & DESIGN TOKENS MAPPING

```text
STITCH DESIGN TOKEN                  NØTUNE SYSTEM MAPPING
─────────────────────────────────────────────────────────────────────────────
Space Grotesk                        Display & Headline Typography
JetBrains Mono                       Telemetry, Code, & Technical Metadata
Inter                                Body Text & Secondary Subtitles
Material Symbols Outlined 20px       System & Navigation Icons
0.25rem - 1.5rem Padding Steps       Compose Dp Spacing (4.dp - 24.dp)
```
