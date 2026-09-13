# NØTUNE Android Widget System Guide

## 1. Overview
NØTUNE features a dedicated Android widget architecture (`com.music.echo.notune.widget`) using Jetpack Glance and Android AppWidgetManager, providing real-time home screen controls and playback telemetry.

---

## 2. Widget Types & Sizes

1. **Now Playing**: Full player controls with song title, artist, album art, and progress bar.
2. **Compact Controls**: Minimal play/pause/skip bar.
3. **Recently Played**: Grid of recent album covers for quick playback resumption.
4. **Favorite Quick Launch**: One-tap access to user's favorite tracks.
5. **AI Recommendation**: Dynamic display of current FLOW recommended track.
6. **Music DNA Summary**: Mini visual radar displaying top genre & listening rhythm.
7. **Couple Mode Sync**: Live status of synchronized Couple listening session.
8. **Downloads**: Storage space monitor & offline track launcher.

---

## 3. Theme Propagation
Widgets consume design tokens from `NoTuneThemeEngine`. When the user changes the application's active Experience Theme, design tokens propagate to the widget layout renderer on the next widget update broadcast.
