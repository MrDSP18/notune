# NØTUNE V2.0.0 — PHYSICAL DEVICE VALIDATION MATRIX

**Validation Objective**: Verify runtime behavior on physical Android hardware and connected emulators prior to production release certification.

---

## PHYSICAL HARDWARE TEST CHECKLIST

| Test Category | Item / Flow | Target Criteria | Status / Verification Method |
| :--- | :--- | :--- | :--- |
| **Lifecycle** | Clean Install & First Launch | App launches without ANR or crash; setup wizard renders Stitch black theme (`#131313`). | `EMULATOR PASS / PHYSICAL PENDING` |
| **Permissions** | Runtime Permission Flow | Prompts for `POST_NOTIFICATIONS`, `READ_MEDIA_AUDIO` / `READ_EXTERNAL_STORAGE`, `BLUETOOTH_CONNECT`. Handles denial gracefully. | `EMULATOR PASS / PHYSICAL PENDING` |
| **Audio Core** | Local Music Playback | ExoPlayer loads local MP3/FLAC/AAC files; seeking scrub bar updates smoothly; track metadata populates. | `EMULATOR PASS / PHYSICAL PENDING` |
| **Background** | Foreground Service Lifecycle | Playback continues in background when app is minimized; media notification shows play/pause/skip and artwork. | `EMULATOR PASS / PHYSICAL PENDING` |
| **Bluetooth** | A2DP & AVRCP Hardware | Bluetooth headset connect/disconnect pauses/resumes audio; hardware play/pause buttons trigger MediaSession commands. | `PHYSICAL PENDING` |
| **Audio Focus** | Interruption Handling | Phone call or transient audio prompt pauses playback and resumes appropriately upon call end. | `PHYSICAL PENDING` |
| **Listen Together** | Real WebSocket Sync | Connects to `wss://metroserverx.meowery.eu/ws`, creates room, joins secondary device, synchronizes play/pause timestamp drift (<50ms). | `BACKEND PASS / PHYSICAL MULTI-DEVICE PENDING` |
| **Widgets** | Glance Home Screen Installation | User installs Now Playing Glance widget on Android home screen; widget updates when track changes. | `EMULATOR PASS / PHYSICAL PENDING` |
| **Deep Links** | HTTPS & Scheme Resolution | Tapping `https://share.notune.fun/room/CODE` opens app directly into `ListenTogetherScreen` with pre-filled room code. | `INTENT PASS / PRODUCTION DOMAIN PENDING` |
| **Offline Mode** | Network Disconnect | App functions seamlessly offline for local media and AI fallback provider without crashing. | `EMULATOR PASS / PHYSICAL PENDING` |

---

## HARDWARE VERIFICATION STEPS FOR TESTING AGENT / DEVELOPER

```bash
# 1. Install release candidate build on connected Android device via ADB
adb install -r app/build/outputs/apk/foss/release/app-universal-foss-release.apk

# 2. Launch main Activity
adb shell am start -n com.music.echo/.MainActivity

# 3. Monitor Logcat for ExoPlayer, WebSocket, and MediaSession logs
adb logcat -s MusicService ExoPlayer RoomWebSocketClient
```
