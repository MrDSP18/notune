-- NØTUNE Production Cloudflare D1 Database Schema
-- 24 Relational Tables for Zero-Cost Platform Backend

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

CREATE TABLE IF NOT EXISTS sessions (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    device_id TEXT NOT NULL,
    token TEXT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS songs (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    artist_name TEXT NOT NULL,
    album_name TEXT,
    duration_sec INTEGER,
    genre TEXT,
    language TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS artists (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    genre TEXT,
    bio TEXT
);

CREATE TABLE IF NOT EXISTS albums (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    artist_id TEXT NOT NULL,
    release_year INTEGER
);

CREATE TABLE IF NOT EXISTS tracks (
    id TEXT PRIMARY KEY,
    song_id TEXT NOT NULL,
    audio_url TEXT NOT NULL,
    format TEXT DEFAULT 'mp3'
);

CREATE TABLE IF NOT EXISTS track_sources (
    id TEXT PRIMARY KEY,
    track_id TEXT NOT NULL,
    provider_name TEXT NOT NULL,
    provider_track_id TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS playlists (
    id TEXT PRIMARY KEY,
    owner_id TEXT NOT NULL,
    title TEXT NOT NULL,
    is_public INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS playlist_items (
    id TEXT PRIMARY KEY,
    playlist_id TEXT NOT NULL,
    song_id TEXT NOT NULL,
    position INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS likes (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    song_id TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS listening_history (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    song_id TEXT NOT NULL,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS friends (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    friend_id TEXT NOT NULL,
    status TEXT DEFAULT 'accepted'
);

CREATE TABLE IF NOT EXISTS friend_requests (
    id TEXT PRIMARY KEY,
    sender_id TEXT NOT NULL,
    receiver_id TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS shares (
    id TEXT PRIMARY KEY,
    short_code TEXT UNIQUE NOT NULL,
    type TEXT NOT NULL,
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

CREATE TABLE IF NOT EXISTS room_members (
    id TEXT PRIMARY KEY,
    room_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS room_events (
    id TEXT PRIMARY KEY,
    room_id TEXT NOT NULL,
    sequence INTEGER NOT NULL,
    event_type TEXT NOT NULL,
    payload_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS messages (
    id TEXT PRIMARY KEY,
    sender_id TEXT NOT NULL,
    receiver_id TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS music_dna (
    user_id TEXT PRIMARY KEY,
    melody_ratio REAL DEFAULT 0.37,
    indie_ratio REAL DEFAULT 0.22,
    hip_hop_ratio REAL DEFAULT 0.16,
    retro_ratio REAL DEFAULT 0.13,
    experimental_ratio REAL DEFAULT 0.12,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lyrics (
    id TEXT PRIMARY KEY,
    song_id TEXT UNIQUE NOT NULL,
    plain_lyrics TEXT,
    synced_lyrics TEXT,
    language TEXT
);

CREATE TABLE IF NOT EXISTS app_versions (
    id TEXT PRIMARY KEY,
    version_name TEXT NOT NULL,
    version_code INTEGER UNIQUE NOT NULL,
    channel TEXT NOT NULL,
    approved INTEGER DEFAULT 0,
    download_url TEXT NOT NULL,
    sha256 TEXT NOT NULL,
    release_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS release_channels (
    id TEXT PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    description TEXT
);
