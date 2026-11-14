import test from "node:test";
import assert from "node:assert/strict";
import { readFileSync, rmSync, existsSync } from "node:fs";
import { DatabaseSync } from "node:sqlite";
import vm from "node:vm";
import { webcrypto } from "node:crypto";
import { TextEncoder, TextDecoder } from "node:util";
import { join } from "node:path";
import { tmpdir } from "node:os";

// 1. Initialize Worker sandbox environment
const workerSource = readFileSync(new URL("../worker.js", import.meta.url), "utf8")
  .replace("export default {", "module.exports = {");
const workerModule = { exports: {} };
vm.runInNewContext(workerSource, {
  module: workerModule,
  exports: workerModule.exports,
  URL,
  Request,
  Response,
  Headers,
  TextEncoder,
  TextDecoder,
  crypto: webcrypto,
  atob,
  btoa,
  fetch,
  setTimeout,
  clearTimeout,
  console
});
const worker = workerModule.exports;

const JWT_SECRET = "production_audit_ultra_secure_jwt_signing_secret_key_2026_d1_native";

// 2. Real SQLite to Cloudflare D1 compatibility bridge
function createD1Adapter(sqliteDb) {
  return {
    prepare(sql) {
      let boundArgs = [];
      return {
        bind(...args) {
          boundArgs = args.map(arg => {
            if (arg === undefined) return null;
            if (typeof arg === "boolean") return arg ? 1 : 0;
            return arg;
          });
          return this;
        },
        async first(column) {
          const stmt = sqliteDb.prepare(sql);
          const row = stmt.get(...boundArgs);
          if (!row) return null;
          if (column) return row[column];
          return row;
        },
        async all() {
          const stmt = sqliteDb.prepare(sql);
          const results = stmt.all(...boundArgs);
          return { results: results || [], success: true };
        },
        async run() {
          const stmt = sqliteDb.prepare(sql);
          const info = stmt.run(...boundArgs);
          return { success: true, meta: { changes: info.changes, last_row_id: Number(info.lastInsertRowid) } };
        }
      };
    },
    async batch(statements) {
      sqliteDb.exec("BEGIN TRANSACTION;");
      try {
        const results = [];
        for (const s of statements) {
          const res = await s.run();
          results.push(res);
        }
        sqliteDb.exec("COMMIT;");
        return results;
      } catch (err) {
        sqliteDb.exec("ROLLBACK;");
        throw err;
      }
    }
  };
}

// 3. Mock R2 Bucket implementation for testing Cloudflare R2 binding
function createMockR2Bucket() {
  const store = new Map();
  return {
    async put(key, value, options = {}) {
      const buffer = value instanceof ArrayBuffer ? Buffer.from(value) : Buffer.from(await value.arrayBuffer());
      store.set(key, {
        body: buffer,
        httpMetadata: options.httpMetadata || {},
        httpEtag: `"${Math.random().toString(36).substring(2)}"`,
        size: buffer.length
      });
      return store.get(key);
    },
    async get(key) {
      const item = store.get(key);
      if (!item) return null;
      return {
        body: item.body,
        httpMetadata: item.httpMetadata,
        httpEtag: item.httpEtag,
        size: item.size,
        writeHttpMetadata(headers) {
          if (item.httpMetadata.contentType) {
            headers.set("Content-Type", item.httpMetadata.contentType);
          }
        }
      };
    },
    async delete(key) {
      store.delete(key);
      return true;
    }
  };
}

// ============================================================================
// AUDIT TEST SUITE
// ============================================================================

test("AUDIT TASK 2: All 4 D1 migrations apply cleanly to SQLite with foreign keys", () => {
  const testDbPath = join(tmpdir(), `notune_audit_mig_${Date.now()}.db`);
  if (existsSync(testDbPath)) rmSync(testDbPath);

  const db = new DatabaseSync(testDbPath);
  db.exec("PRAGMA foreign_keys = ON;");

  const migDir = new URL("../d1-migrations/", import.meta.url);
  const migFiles = [
    "0001_sync_ingestion.sql",
    "0002_api_persistence.sql",
    "0003_authentication.sql",
    "0004_rooms_and_chat.sql"
  ];

  for (const file of migFiles) {
    const sql = readFileSync(new URL(file, migDir), "utf8");
    assert.doesNotThrow(() => {
      db.exec(sql);
    }, `Migration ${file} failed to execute in SQLite`);
  }

  // Verify all 13 required tables
  const expectedTables = [
    "sync_batches",
    "sync_events",
    "shares",
    "social_posts",
    "user_profiles",
    "app_releases",
    "users",
    "user_credentials",
    "sessions",
    "auth_attempts",
    "rooms",
    "room_members",
    "room_messages"
  ];

  const tables = db.prepare("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'").all().map(r => r.name);
  for (const expected of expectedTables) {
    assert.ok(tables.includes(expected), `Missing required D1 table: ${expected}`);
  }

  // Verify key indexes
  const indexes = db.prepare("SELECT name FROM sqlite_master WHERE type='index' AND name NOT LIKE 'sqlite_%'").all().map(r => r.name);
  assert.ok(indexes.includes("idx_sync_events_user_created"));
  assert.ok(indexes.includes("idx_shares_created_at"));
  assert.ok(indexes.includes("idx_users_username"));
  assert.ok(indexes.includes("idx_sessions_refresh_token_hash"));
  assert.ok(indexes.includes("idx_auth_attempts_identifier"));
  assert.ok(indexes.includes("idx_rooms_code"));
  assert.ok(indexes.includes("idx_room_messages_room_time"));

  db.close();
  rmSync(testDbPath);
});

test("AUDIT TASK 3: Database writes and reads persist across SQLite file reconnects", () => {
  const testDbPath = join(tmpdir(), `notune_audit_persist_${Date.now()}.db`);
  if (existsSync(testDbPath)) rmSync(testDbPath);

  // Phase A: Apply schema and insert records
  let db = new DatabaseSync(testDbPath);
  db.exec("PRAGMA foreign_keys = ON;");

  const migDir = new URL("../d1-migrations/", import.meta.url);
  db.exec(readFileSync(new URL("0001_sync_ingestion.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0002_api_persistence.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0003_authentication.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0004_rooms_and_chat.sql", migDir), "utf8"));

  const now = Date.now();
  db.prepare("INSERT INTO users (id, username, email, created_at) VALUES (?, ?, ?, ?)").run("u_test1", "persistent_user", "persist@notune.app", now);
  db.prepare("INSERT INTO user_credentials (user_id, password_hash, salt, iterations, algorithm, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)").run("u_test1", "hash123", "salt123", 100000, "PBKDF2-SHA256", now, now);
  db.prepare("INSERT INTO shares (code, target_id, target_type, user_id, created_at) VALUES (?, ?, ?, ?, ?)").run("sh_test1", "track_99", "track", "u_test1", now);
  db.prepare("INSERT INTO rooms (id, room_code, name, room_type, host_id, host_username, max_members, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)").run("r_test1", "RM001", "Audiophile Room", "COUPLE", "u_test1", "persistent_user", 2, now, now);

  // Close connection (simulating process restart)
  db.close();

  // Phase B: Reopen same database file and verify persistence
  db = new DatabaseSync(testDbPath);
  db.exec("PRAGMA foreign_keys = ON;");

  const user = db.prepare("SELECT * FROM users WHERE id = ?").get("u_test1");
  assert.equal(user?.username, "persistent_user");
  assert.equal(user?.email, "persist@notune.app");

  const creds = db.prepare("SELECT * FROM user_credentials WHERE user_id = ?").get("u_test1");
  assert.equal(creds?.password_hash, "hash123");

  const share = db.prepare("SELECT * FROM shares WHERE code = ?").get("sh_test1");
  assert.equal(share?.target_id, "track_99");

  const room = db.prepare("SELECT * FROM rooms WHERE room_code = ?").get("RM001");
  assert.equal(room?.room_type, "COUPLE");
  assert.equal(room?.max_members, 2);

  db.close();
  rmSync(testDbPath);
});

test("AUDIT TASK 4: Complete Authentication & Web Crypto Lifecycle with Real D1 SQLite", async () => {
  const testDbPath = join(tmpdir(), `notune_audit_auth_${Date.now()}.db`);
  if (existsSync(testDbPath)) rmSync(testDbPath);

  const db = new DatabaseSync(testDbPath);
  db.exec("PRAGMA foreign_keys = ON;");
  const migDir = new URL("../d1-migrations/", import.meta.url);
  db.exec(readFileSync(new URL("0001_sync_ingestion.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0002_api_persistence.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0003_authentication.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0004_rooms_and_chat.sql", migDir), "utf8"));

  const env = {
    DB: createD1Adapter(db),
    JWT_SECRET
  };

  // 1. Registration
  const regRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "audit_tester",
      email: "tester@notune.audio",
      password: "StrongPassword123!",
      displayName: "Audit Tester"
    })
  }), env);

  assert.equal(regRes.status, 201);
  const regJson = await regRes.json();
  assert.ok(regJson.success);
  assert.ok(regJson.tokens.accessToken);
  assert.ok(regJson.tokens.refreshToken);
  const initialAccessToken = regJson.tokens.accessToken;
  const initialRefreshToken = regJson.tokens.refreshToken;

  // 2. Duplicate registration rejects
  const dupRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "audit_tester",
      email: "another@notune.audio",
      password: "StrongPassword123!"
    })
  }), env);
  assert.equal(dupRes.status, 409);

  // 3. Login rejects wrong password
  const badLoginRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      identifier: "audit_tester",
      password: "WrongPassword999"
    })
  }), env);
  assert.equal(badLoginRes.status, 401);

  // 4. Login succeeds with correct password
  const goodLoginRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      identifier: "tester@notune.audio",
      password: "StrongPassword123!"
    })
  }), env);
  assert.equal(goodLoginRes.status, 200);
  const loginJson = await goodLoginRes.json();
  assert.ok(loginJson.tokens.accessToken);

  // 5. GET /api/v1/auth/me returns profile
  const meRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/auth/me", {
    headers: { "Authorization": `Bearer ${initialAccessToken}` }
  }), env);
  assert.equal(meRes.status, 200);
  const meJson = await meRes.json();
  assert.equal(meJson.user.username, "audit_tester");
  assert.equal(meJson.user.email, "tester@notune.audio");

  // 6. Token refresh and rotation
  const refreshRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/auth/refresh", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken: initialRefreshToken })
  }), env);
  assert.equal(refreshRes.status, 200);
  const refreshJson = await refreshRes.json();
  const rotatedAccessToken = refreshJson.tokens.accessToken;
  const rotatedRefreshToken = refreshJson.tokens.refreshToken;
  assert.notEqual(rotatedRefreshToken, initialRefreshToken);

  // 7. Replay attack: Reusing revoked initial refresh token fails with 401
  const replayRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/auth/refresh", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken: initialRefreshToken })
  }), env);
  assert.equal(replayRes.status, 401);

  // 8. Logout revokes rotated token
  const logoutRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/auth/logout", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken: rotatedRefreshToken })
  }), env);
  assert.equal(logoutRes.status, 200);

  // 9. Subsequent refresh after logout fails
  const postLogoutRefresh = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/auth/refresh", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken: rotatedRefreshToken })
  }), env);
  assert.equal(postLogoutRefresh.status, 401);

  db.close();
  rmSync(testDbPath);
});

test("AUDIT TASK 5: Endpoint Authorization and Fail-Closed Security", async () => {
  const testDbPath = join(tmpdir(), `notune_audit_sec_${Date.now()}.db`);
  if (existsSync(testDbPath)) rmSync(testDbPath);

  const db = new DatabaseSync(testDbPath);
  db.exec("PRAGMA foreign_keys = ON;");
  const migDir = new URL("../d1-migrations/", import.meta.url);
  db.exec(readFileSync(new URL("0001_sync_ingestion.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0002_api_persistence.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0003_authentication.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0004_rooms_and_chat.sql", migDir), "utf8"));

  const env = {
    DB: createD1Adapter(db),
    JWT_SECRET
  };

  // Sync rejects unauthenticated request
  const unauthSync = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/sync", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Sync-Protocol-Version": "1.0",
      "X-Idempotency-Key": "00000000-0000-1000-8000-000000000001"
    },
    body: JSON.stringify({ batchId: "00000000-0000-1000-8000-000000000001", protocolVersion: "1.0", eventsCount: 0, events: [] })
  }), env);
  assert.equal(unauthSync.status, 401);

  // Sync rejects forged token
  const forgedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjAwMDAwMDAwLTAwMDAtMTAwMC04MDAwLTAwMDAwMDAwMDAwMSIsImV4cCI6OTk5OTk5OTk5OX0.forged_signature_value";
  const forgedSync = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/sync", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${forgedToken}`,
      "X-Sync-Protocol-Version": "1.0",
      "X-Idempotency-Key": "00000000-0000-1000-8000-000000000001"
    },
    body: JSON.stringify({ batchId: "00000000-0000-1000-8000-000000000001", protocolVersion: "1.0", eventsCount: 0, events: [] })
  }), env);
  assert.equal(forgedSync.status, 401);

  // /api/v1/auth/me rejects missing token
  const unauthMe = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/auth/me"), env);
  assert.equal(unauthMe.status, 401);

  db.close();
  rmSync(testDbPath);
});

test("AUDIT TASK 6: Cloudflare R2 Uploads, Downloads, and Deletions Gateway", async () => {
  const bucket = createMockR2Bucket();
  const env = {
    BUCKET: bucket,
    JWT_SECRET
  };

  // 1. Upload asset to R2
  const testPayload = JSON.stringify({ playlistName: "Audiophile Picks", trackCount: 15 });
  const uploadRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/storage/playlists/backup_001.json", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: testPayload
  }), env);
  assert.equal(uploadRes.status, 201);
  const uploadJson = await uploadRes.json();
  assert.ok(uploadJson.success);
  assert.equal(uploadJson.key, "playlists/backup_001.json");

  // 2. Download asset from R2
  const downloadRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/storage/playlists/backup_001.json"), env);
  assert.equal(downloadRes.status, 200);
  assert.equal(downloadRes.headers.get("Content-Type"), "application/json");
  const content = await downloadRes.text();
  assert.equal(content, testPayload);

  // 3. 404 for unknown asset
  const missingRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/storage/missing_file.jpg"), env);
  assert.equal(missingRes.status, 404);

  // 4. Delete asset from R2
  const deleteRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/storage/playlists/backup_001.json", {
    method: "DELETE"
  }), env);
  assert.equal(deleteRes.status, 200);

  // 5. Subsequent download after delete returns 404
  const postDeleteRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/storage/playlists/backup_001.json"), env);
  assert.equal(postDeleteRes.status, 404);
});

test("AUDIT TASK 7 & 8: Short Link Shares & Multi-Type Rooms & Chat Persistence in D1", async () => {
  const testDbPath = join(tmpdir(), `notune_audit_rooms_${Date.now()}.db`);
  if (existsSync(testDbPath)) rmSync(testDbPath);

  const db = new DatabaseSync(testDbPath);
  db.exec("PRAGMA foreign_keys = ON;");
  const migDir = new URL("../d1-migrations/", import.meta.url);
  db.exec(readFileSync(new URL("0001_sync_ingestion.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0002_api_persistence.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0003_authentication.sql", migDir), "utf8"));
  db.exec(readFileSync(new URL("0004_rooms_and_chat.sql", migDir), "utf8"));

  const env = {
    DB: createD1Adapter(db),
    JWT_SECRET
  };

  // Task 7: Share creation and retrieval
  const sharePost = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/shares", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ targetId: "song_rahman_99", targetType: "track", expiresInDays: 7 })
  }), env);
  assert.equal(sharePost.status, 201);
  const shareJson = await sharePost.json();
  assert.ok(shareJson.code);

  const shareGet = await worker.fetch(new Request(`https://notune-api.workers.dev/api/v1/shares/${shareJson.code}`), env);
  assert.equal(shareGet.status, 200);
  const shareGetJson = await shareGet.json();
  assert.equal(shareGetJson.share.targetId, "song_rahman_99");

  // Task 8: Couple Room creation (max 2 members)
  const coupleRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/rooms", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: "Romantic Duet", roomType: "COUPLE", hostUsername: "DSP" })
  }), env);
  assert.equal(coupleRes.status, 201);
  const coupleJson = await coupleRes.json();
  assert.equal(coupleJson.room.roomType, "COUPLE");
  assert.equal(coupleJson.room.maxMembers, 2);

  // Friends Room creation (max 10 members)
  const friendsRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/rooms", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: "Weekend Hangout", roomType: "FRIENDS", hostUsername: "Alex" })
  }), env);
  assert.equal(friendsRes.status, 201);
  const friendsJson = await friendsRes.json();
  assert.equal(friendsJson.room.roomType, "FRIENDS");
  assert.equal(friendsJson.room.maxMembers, 10);

  // Team Room creation (max 50 members)
  const teamRes = await worker.fetch(new Request("https://notune-api.workers.dev/api/v1/rooms", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: "Sound Engineering Team", roomType: "TEAM", hostUsername: "Chief" })
  }), env);
  assert.equal(teamRes.status, 201);
  const teamJson = await teamRes.json();
  assert.equal(teamJson.room.roomType, "TEAM");
  assert.equal(teamJson.room.maxMembers, 50);

  // Send message in room and retrieve history
  const msgPost = await worker.fetch(new Request(`https://notune-api.workers.dev/api/v1/rooms/${coupleJson.room.roomCode}/messages`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: "DSP", message: "Listening together in 320kbps!" })
  }), env);
  assert.equal(msgPost.status, 201);

  const msgsGet = await worker.fetch(new Request(`https://notune-api.workers.dev/api/v1/rooms/${coupleJson.room.roomCode}/messages`), env);
  assert.equal(msgsGet.status, 200);
  const msgsJson = await msgsGet.json();
  assert.equal(msgsJson.messages.length, 1);
  assert.equal(msgsJson.messages[0].content, "Listening together in 320kbps!");

  db.close();
  rmSync(testDbPath);
});
