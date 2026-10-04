-- NØTUNE Global Cloudflare D1 Database Schema
-- Optimized for zero-cost D1 limits

CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS profiles (
    user_id TEXT PRIMARY KEY,
    display_name TEXT NOT NULL,
    avatar_url TEXT,
    music_dna_json TEXT,
    is_public INTEGER DEFAULT 1,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS devices (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    device_name TEXT NOT NULL,
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS shares (
    id TEXT PRIMARY KEY,
    short_code TEXT UNIQUE NOT NULL,
    type TEXT NOT NULL, -- 'SONG', 'PLAYLIST', 'ROOM', 'PROFILE'
    owner_id TEXT,
    target_id TEXT NOT NULL,
    title TEXT NOT NULL,
    subtitle TEXT,
    artwork_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rooms (
    id TEXT PRIMARY KEY,
    short_code TEXT UNIQUE NOT NULL,
    host_user_id TEXT NOT NULL,
    title TEXT NOT NULL,
    room_type TEXT NOT NULL,
    active_track_json TEXT,
    listener_count INTEGER DEFAULT 1,
    is_active INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS releases (
    id TEXT PRIMARY KEY,
    version_name TEXT NOT NULL,
    version_code INTEGER UNIQUE NOT NULL,
    channel TEXT NOT NULL, -- 'stable', 'beta', 'dev'
    approved INTEGER DEFAULT 0,
    download_url TEXT NOT NULL,
    sha256 TEXT NOT NULL,
    release_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
