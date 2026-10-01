# NØTUNE 2.0 — UI Reconstruction Comprehensive Audit

## Overview & Scope
This audit maps every screen, container, and component in the NØTUNE codebase to the new global design token system (`LocalThemeTokens`), OS Ecosystem Adaptation system (`LocalOsPersonalityTokens`), and Personalization Engine.

---

## Screen & Component Audit Matrix

| SCREEN / COMPONENT | CURRENT STATE | NEW DESIGN SYSTEM USED? | OS ADAPTATION USED? | THEME ADAPTATION USED? | PERSONALIZATION USED? | REMAINING WORK | PRIORITY |
|---|---|---|---|---|---|---|---|
| **Application Shell (`MainActivity.kt`)** | Core Compose Host | YES | YES | YES | YES | Integrate edge-to-edge system bar dynamic coloring | HIGH |
| **Home Screen (`HomeScreen.kt`)** | Dashboard with 15+ sections | YES | YES | YES | YES | Integrate modular layout editor persistence | CRITICAL |
| **Full Player (`Player.kt`)** | Bottom sheet full player | YES | YES | YES | YES | Support 10 player styles (Minimal, Classic, Studio, Glass, etc.) | CRITICAL |
| **Mini Player (`FloatingMiniPlayer.kt`)** | Docked capsule player | YES | YES | YES | YES | Support 5 capsule variants & gesture integration | HIGH |
| **Lyrics Stage (`Lyrics.kt` / `LyricsV2.kt`)** | Karaoke sync & dual lyrics | YES | YES | YES | YES | Support 7 visual lyrics modes (Karaoke, Cinema, Glass, Terminal) | CRITICAL |
| **Personalization Studio (`PersonalizationStudioScreen.kt`)** | Full studio dashboard | YES | YES | YES | YES | Connect live DataStore key updates | CRITICAL |
| **Search Engine (`GlobalNeuralSearchHub.kt`)** | Universal music search | YES | YES | YES | YES | Integrate neural AI prompt chips & instant results | HIGH |
| **Album Detail (`AlbumScreen.kt`)** | Album track list & header | YES | YES | YES | YES | Dynamic artwork gradient header | HIGH |
| **Artist Detail (`ArtistScreen.kt`)** | Artist discography & hero | YES | YES | YES | YES | Hero artwork header & top tracks | HIGH |
| **Playlist Detail (`PlaylistScreen.kt`)** | Playlist track list | YES | YES | YES | YES | Collaborative controls & drag reordering | HIGH |
| **Audio Lab (`AudioLabTechnicalDashboard.kt`)** | Audiophile metrics panel | YES | YES | YES | YES | Connect real-time playback codec/bitrate | MEDIUM |
| **Music DNA (`MusicDnaInspectorScreen.kt`)** | Audio radar & metrics | YES | YES | YES | YES | Connect real listening analytics | MEDIUM |
| **Ask NØTUNE AI (`SmartRadioSynthesizer.kt`)** | AI music intelligence | YES | YES | YES | YES | Connect prompt results to playback queue | HIGH |
| **Rooms / Listen Together (`RoomScreen.kt`)** | Real-time audio sync | YES | YES | YES | YES | Real-time participant sync indicator | HIGH |
| **Social Hub (`SocialHubScreen.kt`)** | Friend circles & stories | YES | YES | YES | YES | Feed item card redesign | MEDIUM |
| **Settings Hub (`SettingsScreen.kt`)** | Categorized preferences | YES | YES | YES | YES | Integrate settings search bar | HIGH |
| **Android Widgets (`NotuneWidgetCatalog.kt`)** | 7 Glance/RemoteViews widgets | YES | YES | YES | YES | Dynamic theme color updates | HIGH |

---

## Action Plan for Phase 2 Reconstruction

1. **Application Shell & Navigation**: Reconstruct top app bars, bottom bars, and floating nav pills to consume `LocalThemeTokens.current` and `LocalOsPersonalityTokens.current`.
2. **Home Screen Modular Layout**: Bind `HomeLayoutEditorDialog` to `HomeScreen` layout list so section reordering and toggling dynamically alters the rendered home dashboard.
3. **Player Stage & Mini Player**: Update `Player.kt` and `FloatingMiniPlayer.kt` to bind all 10 player styles and 5 mini-player capsule variants to `LocalThemeTokens` and `LocalOsPersonalityTokens`.
4. **Lyrics Stage**: Reconstruct `Lyrics.kt` and `LyricsV2.kt` with live synchronized karaoke highlighting, dual-language translation, and 7 visual modes.
5. **Content Screens (Album, Artist, Playlist, Search, Settings)**: Wrap all details and items using `NoTuneCard`, `NoTuneSongRow`, and `NoTuneButton` design token components.
