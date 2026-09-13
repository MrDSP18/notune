# NØTUNE Privacy Architecture & Data Control Policy

## 1. Overview
NØTUNE implements a strict privacy-first architecture (`PrivateSessionManager`). Users retain absolute control over their listening data, AI interaction history, and cloud synchronization.

---

## 2. Key Privacy Capabilities
- **Private Session Mode**: One-tap toggle that completely halts listening history logging, taste engine profile updates, and recommendation telemetry.
- **AI Privacy Filter**: Pre-filters prompts dispatched to cloud AI providers, removing user account identifiers, IP addresses, and exact location data.
- **Local Data Export**: One-tap export of local history, favorites, playlists, and Music DNA metrics into standardized JSON format.
- **Local Data Wipe**: Instant reset of local database, preferences DataStore, and cached telemetry.
- **Cloud Account Deletion**: Dispatches deletion request to backend API to purge user record, sessions, and synchronized profiles from PostgreSQL.
