# NØTUNE Database Architecture & Schema Reference

## 1. Overview
NØTUNE uses a dual database strategy:
1. **Local Client Database**: Android Room SQLite database (`core` module) providing offline-first persistence for track metadata, local history, playlists, room cache, and Music DNA metrics.
2. **Cloud Database**: PostgreSQL (`notune-backend/schema.sql` and versioned DDL scripts in `notune-backend/migrations/`) for unified user identity, multi-device sessions, encrypted profile sync, and real-time social room membership.

---

## 2. Versioned Migration DDL Directory
- **`001_initial_schema.sql`**: Initial creation of `users`, `user_sessions`, `user_profiles`, `rooms`, `room_members`, and `sync_events` tables with cascade deletion rules.
- **`002_indexes_and_constraints.sql`**: High-performance indexes for OAuth lookup, session device resolution, room codes, telemetry sync, and check constraints (`chk_expires_future`).

---

## 3. PostgreSQL Cloud Database Schema Summary

### Table: `users`
- `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
- `email`: `VARCHAR(255) UNIQUE`
- `google_id`: `VARCHAR(255) UNIQUE`
- `github_id`: `VARCHAR(255) UNIQUE`
- `phone_number`: `VARCHAR(50) UNIQUE`
- `display_name`: `VARCHAR(100) NOT NULL`
- `avatar_url`: `TEXT`
- `created_at`, `updated_at`: `TIMESTAMP WITH TIME ZONE`

### Table: `user_sessions`
- `id`: `UUID PRIMARY KEY DEFAULT gen_random_uuid()`
- `user_id`: `UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE`
- `device_id`: `VARCHAR(255) NOT NULL`
- `device_name`: `VARCHAR(255)`
- `refresh_token_hash`: `VARCHAR(255) NOT NULL`
- `is_revoked`: `BOOLEAN DEFAULT FALSE`
- `expires_at`: `TIMESTAMP WITH TIME ZONE NOT NULL`

### Table: `user_profiles`
- `user_id`: `UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE`
- `theme_key`: `VARCHAR(50) DEFAULT 'NOTUNE_PURE'`
- `typography_key`: `VARCHAR(50) DEFAULT 'NOTUNE_SANS'`
- `music_dna_json`: `JSONB DEFAULT '{}'::jsonb`
- `preferences_json`: `JSONB DEFAULT '{}'::jsonb`

### Table: `rooms` & `room_members`
- `id`: `UUID PRIMARY KEY`
- `room_code`: `VARCHAR(12) UNIQUE NOT NULL`
- `host_user_id`: `UUID REFERENCES users(id)`
- `title`: `VARCHAR(100) NOT NULL`
- `is_private`: `BOOLEAN DEFAULT FALSE`
- `max_members`: `INT DEFAULT 50`

### Table: `sync_events`
- `id`: `UUID PRIMARY KEY`
- `user_id`: `UUID REFERENCES users(id) ON DELETE CASCADE`
- `device_id`: `VARCHAR(255) NOT NULL`
- `event_type`: `VARCHAR(50) NOT NULL`
- `payload_json`: `JSONB NOT NULL`
- `client_timestamp`: `BIGINT NOT NULL`

---

## 4. Database Security Controls
- **Encryption at Rest**: Sensitive authentication secrets (tokens, room keys) stored via Android Keystore `SecureStorageManager`.
- **Backup Isolation**: Database backup policies isolate sensitive session tables.
