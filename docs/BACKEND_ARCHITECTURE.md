# NØTUNE Backend Architecture & Integration Guide

## 1. Overview
The NØTUNE backend is designed as an offline-first, modular service architecture backed by PostgreSQL. The client application strictly maintains local Room SQLite databases as the primary source of truth, queuing events in an offline outbox pattern (`CloudSyncManager`) and pushing them asynchronously when backend connectivity is available.

---

## 2. API Architecture Specification
- **Protocol**: HTTPS / REST + WebSocket (WSS for real-time Rooms & Couple Mode).
- **Base Endpoint**: `https://api.notune.app/v1`
- **Data Serialization**: JSON UTF-8.
- **Authentication**: JWT Bearer Token (`Authorization: Bearer <access_token>`).

---

## 3. Core Modules & Endpoint Endpoints

### A. Identity & Session Management
- `POST /v1/auth/google`: Authenticate with Google ID token, return NØTUNE session token.
- `POST /v1/auth/github`: Authenticate with GitHub authorization code.
- `POST /v1/auth/phone/request`: Request SMS OTP.
- `POST /v1/auth/phone/verify`: Verify SMS OTP code.
- `POST /v1/auth/refresh`: Rotate refresh token and issue fresh access token.
- `POST /v1/auth/logout`: Revoke current device session.
- `GET /v1/auth/sessions`: List active device sessions.
- `DELETE /v1/auth/sessions/{device_id}`: Remotely terminate specific device session.

### B. Telemetry & Taste Sync
- `POST /v1/sync/batch`: Dispatch queued telemetry outbox payloads (`TASTE_UPDATE`, `HISTORY_BATCH`, `PREFERENCE_CHANGE`).
- `GET /v1/profile/dna`: Fetch cloud-backed Music DNA telemetry.

### C. Social Rooms & Couple Mode
- `POST /v1/rooms`: Create new social room (Public, Private, Couple).
- `GET /v1/rooms`: Search and list public listening rooms.
- `WSS /v1/rooms/{room_code}/ws`: Real-time WebSocket connection for synchronized queue voting, playback state, and E2E encrypted messages.

---

## 4. Local vs. Server Authority
- **Playback & Audio**: Client-authoritative (Media3 ExoPlayer).
- **Room State & Queue**: Server-authoritative (Backend validates host/member permissions and broadcasts synchronized state).
- **E2E Encryption**: Client-side AES-256-GCM. Backend handles encrypted payload delivery without access to plaintext room keys.
