-- ====================================================================
-- NØTUNE Cloud Backend Database Schema (PostgreSQL)
-- Optimized for Supabase, Neon.tech, Render, and self-hosted PostgreSQL.
-- ====================================================================

-- 1. Unified User Identity Table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE,
    google_id VARCHAR(255) UNIQUE,
    github_id VARCHAR(255) UNIQUE,
    phone_number VARCHAR(50) UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    avatar_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_google ON users(google_id);
CREATE INDEX IF NOT EXISTS idx_users_github ON users(github_id);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone_number);

-- 2. Device & Session Token Rotation
CREATE TABLE IF NOT EXISTS user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id VARCHAR(255) NOT NULL,
    device_name VARCHAR(255),
    refresh_token_hash VARCHAR(255) NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_active_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sessions_user ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_device ON user_sessions(device_id);

-- 3. Encrypted Profile & Taste Sync Table
CREATE TABLE IF NOT EXISTS user_profiles (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    theme_key VARCHAR(50) DEFAULT 'NOTUNE_PURE',
    typography_key VARCHAR(50) DEFAULT 'NOTUNE_SANS',
    music_dna_json JSONB DEFAULT '{}'::jsonb,
    preferences_json JSONB DEFAULT '{}'::jsonb,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Social Rooms Table
CREATE TABLE IF NOT EXISTS rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_code VARCHAR(12) UNIQUE NOT NULL,
    host_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    is_private BOOLEAN DEFAULT FALSE,
    max_members INT DEFAULT 50,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_rooms_code ON rooms(room_code);

-- 5. Room Members & Permissions
CREATE TABLE IF NOT EXISTS room_members (
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) DEFAULT 'MEMBER', -- HOST, COHOST, MODERATOR, MEMBER
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (room_id, user_id)
);

-- 6. Offline-First Synchronization Events Table
CREATE TABLE IF NOT EXISTS sync_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL, -- TASTE_UPDATE, HISTORY_BATCH, PREFERENCE_CHANGE
    payload_json JSONB NOT NULL,
    client_timestamp BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sync_events_user ON sync_events(user_id);
