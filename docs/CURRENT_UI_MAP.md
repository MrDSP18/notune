# NØTUNE — CURRENT UI ARCHITECTURE MAP

**Repository**: `/home/dharan-25486/Documents/music/V2/notune`  
**Package Namespace**: `echo.music.iad1tya` / `com.music.echo`  

---

## 1. ARCHITECTURE & SCREEN NAVIGATION MAP

```text
Application (App.kt)
│
└── MainActivity.kt (Single Activity Architecture with Jetpack Compose & NavHost)
    │
    ├── App Shell Navigation (com/music/echo/ui/screens/HomeScreen.kt)
    │   ├── Home Dashboard (HomeScreen.kt)
    │   ├── Search (SearchScreen.kt)
    │   ├── Library (LibraryScreen.kt)
    │   ├── Public Discovery (PublicDiscoveryScreen.kt)
    │   ├── Rooms / Listen Together (ListenTogetherScreen.kt)
    │   └── Ask NØTUNE AI DJ (AskNoTuneScreen.kt)
    │
    ├── Persistent Player Container (com/music/echo/ui/player/AdaptivePlayerContainer.kt)
    │   ├── Mini Player (Audio Resolution Badges + Telemetry Status)
    │   └── Full Player (Waveform Visualizer + Lossless Format Chips)
    │
    ├── Design Tokens & Components (com/music/echo/ui/components/NoTuneTelemetryComponents.kt)
    │   ├── SystemTelemetryHeader
    │   ├── AudioResolutionBadge
    │   └── VibeRadarCard
    │
    └── Theme Engine System (com/music/echo/ui/theme/ThemeEngine.kt)
        ├── NØTUNE STITCH & TUNE (Default OLED Pitch Black #131313 & Crimson #FF002E)
        └── Theme Lab (20 Custom Visual Systems)
```

---

## 2. SCREEN & VIEWMODEL CONTRACT MATRIX

| Screen Area | File Location | ViewModel / Manager Contract | UI Status |
| :--- | :--- | :--- | :--- |
| **Home Dashboard** | `com/music/echo/ui/screens/HomeScreen.kt` | `HomeViewModel.kt` | **Stitch & Tune Redesigned** |
| **Ask NØTUNE (AI DJ)** | `com/music/echo/social/ui/AskNoTuneScreen.kt` | `AiViewModel.kt` | **Stitch & Tune Redesigned** |
| **Listen Together** | `com/music/echo/ui/screens/ListenTogetherScreen.kt` | `ListenTogetherManager.kt` | **Stitch & Tune Redesigned** |
| **Library & Storage** | `com/music/echo/ui/screens/LibraryScreen.kt` | `LibraryViewModel.kt` | **Stitch & Tune Redesigned** |
| **Player Container** | `com/music/echo/ui/player/AdaptivePlayerContainer.kt` | `PlayerViewModel.kt` / Media3 | **Stitch & Tune Redesigned** |
| **Public Discovery** | `com/music/echo/notune/rooms/ui/PublicDiscoveryScreen.kt` | `RoomsViewModel.kt` | **Stitch & Tune Redesigned** |
| **Theme Engine** | `com/music/echo/ui/theme/ThemeEngine.kt` | `ThemeEngine.kt` | **Stitch & Tune Redesigned** |
