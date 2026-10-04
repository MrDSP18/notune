# NØTUNE v3.1.0 Universal FOSS Release Notes

NØTUNE V3 is a free, ad-free, AI-native Personal Music Operating System featuring high-fidelity sound, real-time room synchronization, synchronized lyrics, adaptive recommendation queues, and zero cloud LLM costs.

---

## 🌐 Production Web Platform & Desktop Web Player

- **Live Web URL**: [https://notune-web.pages.dev](https://notune-web.pages.dev)
- **Live Desktop Player**: [https://notune-web.pages.dev/player](https://notune-web.pages.dev/player)
- **Production Edge API**: [https://notune-api.dharansundarapandiyan24.workers.dev](https://notune-api.dharansundarapandiyan24.workers.dev)

### Highlights & Platform Capabilities
1. **Desktop Web Player**:
   - Built specifically for laptop/desktop browsers with CSS Grid layout, deep dark `#090A0F` aesthetic, brand red accents, and glassmorphism cards.
   - Real Web Audio engine (`HTMLMediaElement`) supporting Play, Pause, Seek, Volume, Prev/Next, Shuffle, Repeat, Like, and background SPA route navigation.
   - OS Media Session API (`navigator.mediaSession`) integration and global keyboard media shortcuts (Space, Arrow keys, M, L, Q).

2. **Adaptive Queue System**:
   - Contextual recommendation pipeline (`POST /api/v1/recommendations/adaptive`) that automatically appends upcoming tracks as playback continues based on song context, history, and mood.

3. **Synchronized Lyrics**:
   - Real-time LRC timestamp parser with line highlighting, auto-scroll, translation toggle, and fullscreen overlay mode.

4. **Listen Together Rooms**:
   - Cloudflare Durable Object WebSocket hibernation engine (`wss://notune-api.../api/v1/rooms/:roomId/ws`) providing real-time playback synchronization, host controls, listener list, and room chat.

5. **NØ AI Architecture**:
   - **Android**: Native LiteRT-LM (Gemma-2B / Qwen) on-device runtime running 100% locally.
   - **Web Platform**: On-device multi-dimensional acoustic vector matrix computing cosine similarity across genre, valence, energy, and acoustic spectrum without cloud LLM costs.

6. **Social Feed & User Profiles**:
   - Connected to Cloudflare Edge API (`/api/v1/social/feed` & `/api/v1/profiles/me`) for friend activity, user avatars, and Music DNA acoustic ratios.

7. **Universal Sharing & Android App Links**:
   - Rich preview cards for `/s/*`, `/p/*`, `/r/*`, `/u/*` with deep-linking `notune://open/...`, web play fallback, and Digital Asset Links (`assetlinks.json`).

8. **PWA & Offline Resilience**:
   - Web App Manifest (`manifest.json`) and Service Worker (`sw.js`) for offline app shell caching.

---

## ⚠️ Known Limitations & Deployment Notes

1. **Custom Domain (`notune.app`)**:
   - The custom domain `notune.app` is currently unregistered by the user (external domain availability blocker). The canonical production web platform is live and verified at `https://notune-web.pages.dev`.
2. **Android Release Signing**:
   - Keystore file `keystore.jks` was not set in the build environment. The release APK artifact was compiled using the persistent fallback keystore (`debug.keystore`).
3. **Web NØ AI Engine**:
   - Web NØ AI uses an on-device acoustic vector similarity matrix engine, whereas Android uses the native LiteRT-LM model.

---

## 📦 Release Artifact Checksums

```text
Version Name: 3.1.0
Version Code: 30100
Build Variant: Universal FOSS Release (ProGuard / R8 Minified)
Artifact Filename: app-universal-foss-release.apk
File Size: 63 MB (65,837,419 bytes)
SHA-256 Checksum: 882bb492897924bd788d2a45be42f0cd839acd9e1ac7e292076abf7386cd3a35
```
