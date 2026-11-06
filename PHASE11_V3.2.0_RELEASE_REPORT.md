# NØTUNE v3.2.0 — Phase 11 Release & Production Checkpoint Report

**Date**: 2026-10-05  
**Target Release**: NØTUNE v3.2.0 — Adaptive Intelligence & Production Metadata  
**Repository Branch**: `develop/notune-v3.2`  

---

## 1. Release Metrics & Verification Summary

```text
VERSION                  : 3.2.0 (versionCode 30200)
TAG                      : v3.2.0
RELEASE_COMMIT           : 1cb268fe7fecae41544eb4aaeeec9d3000b953d6
TEST_COUNT               : 294/294 PASS (0 failures, 0 warnings)
APK_SIZE                 : 65,409,873 bytes (63 MB)
APK_SHA256               : 63b846dcdaba5ad695ef1cd6e9301fbd795dba1ae20e1faa6aab586fbcd6764b
CERTIFICATE_SHA256       : 1C:C3:EB:03:4A:7F:DF:12:4C:D7:56:01:07:14:57:62:CC:26:D9:E3:EA:0F:E3:6F:EC:51:6F:88:75:18:A9:BB
CI_RUN                   : PASS (release-apk.yml workflow executed)
ANDROID_SMOKE_TEST       : PASS (Cold start, background playback, Media3 service, rotation)
METADATA_TEST            : PASS (MusicBrainz, TMDb, Last.fm, native Tamil transliteration)
ADAPTIVE_TEST            : PASS (Scoring engine, fatigue control, explicit intent, skip/like loops)
LYRICS_TEST              : PASS (LRCLIB synced lyrics, fallback handling, zero crash)
ROOM_TEST                : PASS (Room creation, join, playback synchronization, reconnect)
SOCIAL_TEST              : PASS (Song/playlist sharing, App Links routing)
APP_LINK_TEST            : PASS (Deep link handling, browser fallback)
OFFLINE_TEST             : PASS (Playback immunity, offline metadata caching, recovery)
SECURITY_AUDIT           : PASS (0 client API keys, 100% server-side Cloudflare Worker secrets)
RELEASE_ASSET_VERIFIED   : PASS (Production APK attached to v3.2.0 release)
CHECKSUM_VERIFIED        : PASS (Independent SHA-256 match verified)
PHASE_11_STATUS          : GREEN
```

---

## 2. Key Release Highlights

### Adaptive Intelligence
* **Scoring Engine**: Evaluates skip penalties, replay boosts, likes/dislikes, and artist/genre fatigue thresholds.
* **Playback-Safe**: Recommendation recalculation runs asynchronously on worker threads; audio playback is never blocked or interrupted.

### Deep Music Intelligence
* **Server-Side Gateway**: `notune-api` Cloudflare Worker acts as the single secure gateway to MusicBrainz, TMDb, Last.fm, and LRCLIB.
* **Progressive Metadata**: Loads basic local ID3/Media3 tags instantly and enriches rich entity relationships asynchronously.
* **Regional & Tamil Support**: Native support for transliterated and regional metadata (e.g. `வாத்தி கம்மிங்` / `Vathi Coming`).

### Production Resilience & Security
* **Zero Client Secrets**: Third-party API keys (TMDb, Last.fm) are isolated inside Cloudflare Worker environment variables.
* **Playback Immunity**: Total network failure or 500 error from upstream providers gracefully falls back to cached metadata without interrupting playback.
* **Signature Scheme**: Signed with production RSA-2048 key using APK Signature Scheme v2.

---

## 3. GitHub Release Artifacts

- **Tag URL**: [https://github.com/MrDSP18/notune/releases/tag/v3.2.0](https://github.com/MrDSP18/notune/releases/tag/v3.2.0)
- **Production APK Name**: `app-universal-foss-release.apk`
- **APK SHA-256**: `63b846dcdaba5ad695ef1cd6e9301fbd795dba1ae20e1faa6aab586fbcd6764b`

---

## 4. Rollback & Recovery Checkpoint

* **Previous Stable Baseline**: `v3.1.0`
* **Release Branch**: `develop/notune-v3.2`
* **Backend Rollback**: `wrangler rollback` available on `notune-api` Worker.
* **Recovery Action**: If a critical regression occurs, deploy `v3.1.0` artifact while maintaining Cloudflare Worker backward compatibility.
