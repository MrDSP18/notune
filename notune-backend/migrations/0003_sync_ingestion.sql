-- Cloudflare D1 sync outbox persistence.
-- User identity comes exclusively from a verified JWT, never from request JSON.

CREATE TABLE IF NOT EXISTS sync_batches (
  user_id TEXT NOT NULL,
  idempotency_key TEXT NOT NULL,
  request_hash TEXT NOT NULL,
  created_at INTEGER NOT NULL,
  PRIMARY KEY (user_id, idempotency_key)
);

CREATE TABLE IF NOT EXISTS sync_events (
  user_id TEXT NOT NULL,
  event_id TEXT NOT NULL,
  event_type TEXT NOT NULL,
  payload_json TEXT NOT NULL,
  client_timestamp INTEGER NOT NULL,
  event_hash TEXT NOT NULL,
  idempotency_key TEXT NOT NULL,
  created_at INTEGER NOT NULL,
  PRIMARY KEY (user_id, event_id)
);

CREATE INDEX IF NOT EXISTS idx_sync_events_user_created
  ON sync_events(user_id, created_at);

CREATE INDEX IF NOT EXISTS idx_sync_events_user_type
  ON sync_events(user_id, event_type);
