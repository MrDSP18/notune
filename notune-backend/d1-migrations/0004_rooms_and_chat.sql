-- Migration: 0004_rooms_and_chat.sql
-- Cloudflare D1 schema for Couple, Friends, and Team Listen-Together Rooms and Real-Time Chat

CREATE TABLE IF NOT EXISTS rooms (
    id TEXT PRIMARY KEY,
    room_code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    room_type TEXT NOT NULL DEFAULT 'FRIENDS', -- 'COUPLE', 'FRIENDS', 'TEAM', 'PUBLIC'
    host_id TEXT NOT NULL,
    host_username TEXT NOT NULL,
    current_track_id TEXT,
    current_track_title TEXT,
    current_track_artist TEXT,
    current_track_duration INTEGER DEFAULT 0,
    current_track_thumbnail TEXT,
    is_playing INTEGER DEFAULT 0,
    position_ms INTEGER DEFAULT 0,
    max_members INTEGER DEFAULT 10,
    is_active INTEGER DEFAULT 1,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rooms_code ON rooms(room_code);
CREATE INDEX IF NOT EXISTS idx_rooms_active_type ON rooms(is_active, room_type);

CREATE TABLE IF NOT EXISTS room_members (
    room_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    username TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'MEMBER', -- 'HOST', 'CO_HOST', 'MEMBER'
    is_connected INTEGER DEFAULT 1,
    joined_at INTEGER NOT NULL,
    last_seen_at INTEGER NOT NULL,
    PRIMARY KEY (room_id, user_id),
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_room_members_room ON room_members(room_id);

CREATE TABLE IF NOT EXISTS room_messages (
    id TEXT PRIMARY KEY,
    room_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    username TEXT NOT NULL,
    content TEXT NOT NULL,
    message_type TEXT NOT NULL DEFAULT 'CHAT', -- 'CHAT', 'REACTION', 'SYSTEM', 'TRACK_SUGGESTION'
    created_at INTEGER NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_room_messages_room_time ON room_messages(room_id, created_at);
