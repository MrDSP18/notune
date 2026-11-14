-- Cloudflare D1 real persistence schema for shares, social posts, user profiles, and app releases.
-- Replaces simulated/mock endpoints with persistent, queryable SQLite tables.

CREATE TABLE IF NOT EXISTS shares (
  code TEXT PRIMARY KEY,
  target_id TEXT NOT NULL,
  target_type TEXT NOT NULL DEFAULT 'track',
  user_id TEXT,
  created_at INTEGER NOT NULL,
  expires_at INTEGER
);

CREATE INDEX IF NOT EXISTS idx_shares_created_at
  ON shares(created_at DESC);

CREATE TABLE IF NOT EXISTS social_posts (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  user_display_name TEXT NOT NULL,
  avatar_url TEXT,
  action_text TEXT NOT NULL,
  track_id TEXT,
  track_title TEXT,
  track_artist TEXT,
  created_at INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_social_posts_created_at
  ON social_posts(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_social_posts_user_id
  ON social_posts(user_id);

CREATE TABLE IF NOT EXISTS user_profiles (
  user_id TEXT PRIMARY KEY,
  display_name TEXT NOT NULL,
  avatar_url TEXT,
  music_dna_json TEXT NOT NULL DEFAULT '{}',
  liked_count INTEGER NOT NULL DEFAULT 0,
  followers_count INTEGER NOT NULL DEFAULT 0,
  following_count INTEGER NOT NULL DEFAULT 0,
  updated_at INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS app_releases (
  version_name TEXT NOT NULL,
  version_code INTEGER NOT NULL,
  platform TEXT NOT NULL DEFAULT 'android',
  channel TEXT NOT NULL DEFAULT 'stable',
  approved INTEGER NOT NULL DEFAULT 1,
  download_url TEXT NOT NULL,
  sha256 TEXT NOT NULL,
  release_notes TEXT,
  created_at INTEGER NOT NULL,
  PRIMARY KEY (platform, channel, version_code)
);

CREATE INDEX IF NOT EXISTS idx_app_releases_lookup
  ON app_releases(platform, channel, approved, version_code DESC);
