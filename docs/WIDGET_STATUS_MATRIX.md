# NØTUNE V2.0.0 — APP WIDGET STATUS MATRIX

**Document Version**: 2.0.0  
**Framework**: Android Glance (Jetpack Compose AppWidget)  
**Package Path**: `com.music.echo.widget`

---

## IMPLEMENTATION & AUDIT STATUS

| AppWidget Name | Status | Implementation File | Verification Details |
| :--- | :--- | :--- | :--- |
| **Now Playing Widget** | `REAL IMPLEMENTED` | `com/music/echo/widget/NowPlayingWidget.kt` | Interactive Glance widget displaying current track title, artist, artwork, play/pause state, and skip controls. Registered in `AndroidManifest.xml`. |
| **Compact Player Widget** | `REAL IMPLEMENTED` | `com/music/echo/widget/CompactPlayerWidget.kt` | Minimal 2x1 Glance widget for quick playback control and track status. Registered in `AndroidManifest.xml`. |
| **Quick Controls Widget** | `REAL IMPLEMENTED` | `com/music/echo/widget/QuickControlsWidget.kt` | 4x1 toolbar widget providing instant access to Flow mode, Shuffle toggle, Like, and Play/Pause. |
| **Lyrics Widget** | `PARTIALLY IMPLEMENTED` | `com/music/echo/widget/LyricsWidget.kt` | Displays current active lyric line synced via BroadcastReceiver. |
| **Music DNA Widget** | `DESIGN ONLY / CONCEPT PREVIEW` | N/A (Compose Screen Component) | Visual radar card displayed within `MusicDnaScreen.kt`. Not yet exported as an installed Glance AppWidget receiver. |
| **NØTUNE FLOW Widget** | `DESIGN ONLY / CONCEPT PREVIEW` | N/A (Compose Screen Component) | Algorithmic flow launcher UI card. Exists in-app as Compose UI; Glance AppWidget receiver planned for v2.1. |
| **Friends Listening Widget** | `DESIGN ONLY / CONCEPT PREVIEW` | N/A (Compose Screen Component) | Social activity feed preview card. Exists in-app as Compose UI; Glance AppWidget receiver planned for v2.1. |

---

## MANIFEST REGISTRATION EVIDENCE

```xml
<!-- AndroidManifest.xml AppWidget Receivers -->
<receiver
    android:name="com.music.echo.widget.NowPlayingWidgetReceiver"
    android:exported="true">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/now_playing_widget_info" />
</receiver>
```
