-- ====================================================================
-- NØTUNE Migration 002: Indexes and Constraints Optimization
-- ====================================================================

CREATE INDEX IF NOT EXISTS idx_users_google ON users(google_id);
CREATE INDEX IF NOT EXISTS idx_users_github ON users(github_id);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone_number);

CREATE INDEX IF NOT EXISTS idx_sessions_user ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_device ON user_sessions(device_id);

CREATE INDEX IF NOT EXISTS idx_rooms_code ON rooms(room_code);
CREATE INDEX IF NOT EXISTS idx_sync_events_user ON sync_events(user_id);

-- Constraint verification for user_sessions token revocation
ALTER TABLE user_sessions ADD CONSTRAINT chk_expires_future CHECK (expires_at > created_at);
