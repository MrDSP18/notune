# NØTUNE Release & Truth Checklist

## Pre-Release Verification & Build Artifacts

- [x] **Truth-First Audit**: Verified no fake backend success, fake AI answers, or hardcoded mock data exist in production code paths.
- [x] **Zero-Config AI Router**: Verified app operates seamlessly using local basic intelligence fallback when no cloud AI API keys are configured.
- [x] **Universal Language Engine**: Verified 8 independent language domains and script fallback capabilities.
- [x] **Design Engine**: Verified 20 experience themes with Light, Dark, and System modes.
- [x] **New Logo & Visual Enhancements**: Futuristic slashed orbital ring vector logo with ambient radial glow and visualizer bar previews.
- [x] **Privacy Center**: Verified Private Session toggle, local JSON export, and local data wipe.
- [x] **Gradle Build Completion**: `BUILD SUCCESSFUL in 40s` (`./gradlew assembleUniversalFossDebug`).
- [x] **Verified APK Artifact**:
  - Path: `app/build/outputs/apk/universalFoss/debug/app-universal-foss-debug.apk`
  - File Size: `111 MB`
  - SHA-256: `65f715d95122b8fb658f9cc6f09b803b6abd684938606caf295f3d445ee17abe`
- [ ] **Live Backend Hosting**: Multi-client Social Rooms, Couple Mode sync, and PostgreSQL cloud sync require live server hosting (`BACKEND REQUIRED`).
- [ ] **Local Database-at-Rest Encryption**: Room SQLite database file is stored unencrypted on local storage (`NOT IMPLEMENTED`).
- [ ] **Full Multi-Device E2E PKI**: AES-256-GCM cipher logic is implemented, but multi-device PKI key exchange protocol is `NOT VERIFIED`.
- [ ] **Commercial Lyrics Licensing Compliance**: Technical REST APIs work via LrcLib and KuGou, but commercial redistribution rights remain `NOT VERIFIED`.
- [ ] **Real-Device Matrix Verification**: Requires physical hardware testing on Android 12, 13, 14, 15 devices (`NOT VERIFIED`).
