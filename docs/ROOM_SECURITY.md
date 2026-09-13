# NØTUNE Room Security & End-to-End Encryption Protocol

## 1. Overview
NØTUNE Social Rooms (Private, Friends, Couple) support End-to-End Encryption (E2E) using `RoomEncryptionManager`. Chat messages and room payloads are encrypted client-side using AES-256-GCM prior to network transmission, ensuring that servers act purely as encrypted message relays.

---

## 2. Key Agreement & Cryptographic Specification
- **Cipher**: AES-256-GCM (Galois/Counter Mode).
- **Key Derivation**: HKDF-SHA256 from shared room secret + epoch timestamp.
- **Initialization Vector (IV)**: 12-byte cryptographically secure random IV generated per message payload.
- **Authentication Tag**: 128-bit GCM authentication tag attached to ciphertext for integrity verification.
- **Replay Protection**: Monotonically increasing sequence counters and timestamp validation.

---

## 3. Threat Model & Verification Status
- **Client Status**: Client-side AES-256-GCM encryption and decryption logic implemented and verified in `RoomEncryptionManager.kt`.
- **Server Status**: PostgreSQL schema (`rooms`, `room_members`) stores room metadata and encrypted message payloads (`encrypted_content`).
- **Multi-Device Status**: Real-time room synchronization across active remote clients requires deployment of backend WebSocket relay server (`BACKEND REQUIRED`).
