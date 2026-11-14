import test from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import vm from "node:vm";
import { webcrypto } from "node:crypto";
import { TextEncoder, TextDecoder } from "node:util";
import { Buffer } from "node:buffer";

const source = readFileSync(new URL("../worker.js", import.meta.url), "utf8")
  .replace("export default {", "module.exports = {");
const module = { exports: {} };
vm.runInNewContext(source, {
  module,
  exports: module.exports,
  URL,
  Request,
  Response,
  Headers,
  TextEncoder,
  TextDecoder,
  crypto: webcrypto,
  atob,
  fetch,
  setTimeout,
  clearTimeout,
  console
});
const worker = module.exports;
const secret = "test-only-sync-signing-secret-at-least-32-bytes";

function base64url(value) {
  return Buffer.from(value).toString("base64url");
}

async function signToken(userId) {
  const header = base64url(JSON.stringify({ alg: "HS256", typ: "JWT" }));
  const claims = base64url(JSON.stringify({ id: userId, exp: Math.floor(Date.now() / 1000) + 300 }));
  const input = header + "." + claims;
  const key = await webcrypto.subtle.importKey(
    "raw", new TextEncoder().encode(secret), { name: "HMAC", hash: "SHA-256" }, false, ["sign"]
  );
  const signature = await webcrypto.subtle.sign("HMAC", key, new TextEncoder().encode(input));
  return input + "." + base64url(Buffer.from(signature));
}

function fakeDb(expectedRequestHash, eventHash) {
  const db = {
    batchCalls: 0,
    async batch(statements) {
      this.batchCalls++;
      assert.equal(statements.length, 2);
      return [];
    },
    prepare(sql) {
      return {
        bind() {
          return {
            async first() {
              return { request_hash: expectedRequestHash };
            },
            async all() {
              return { results: [{ event_id: "22222222-2222-4222-8222-222222222222", event_hash: eventHash }] };
            }
          };
        }
      };
    }
  };
  return db;
}

async function makeBatch() {
  const event = {
    eventId: "22222222-2222-4222-8222-222222222222",
    eventType: "PLAYBACK_HISTORY_LOGGED",
    payload: JSON.stringify({ songId: "song-1", action: "PLAYBACK_FINISHED" }),
    timestamp: Date.now()
  };
  const eventHashInput = JSON.stringify({
    eventId: event.eventId,
    eventType: event.eventType,
    payload: event.payload,
    timestamp: event.timestamp
  });
  const eventHash = Buffer.from(await webcrypto.subtle.digest("SHA-256", new TextEncoder().encode(eventHashInput))).toString("hex");
  const batchId = "33333333-3333-4333-8333-333333333333";
  const normalized = [{ ...event, eventHash }];
  const requestHashInput = JSON.stringify({ batchId, protocolVersion: "1.0", events: normalized });
  const requestHash = Buffer.from(await webcrypto.subtle.digest("SHA-256", new TextEncoder().encode(requestHashInput))).toString("hex");
  return {
    event,
    eventHash,
    requestHash,
    batchId,
    body: JSON.stringify({
      batchId,
      protocolVersion: "1.0",
      eventsCount: 1,
      events: [event],
      clientTimestamp: Date.now()
    })
  };
}

test("sync endpoint rejects requests without a bearer token", async () => {
  const request = new Request("https://example.test/api/v1/sync", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Sync-Protocol-Version": "1.0",
      "X-Idempotency-Key": "33333333-3333-4333-8333-333333333333"
    },
    body: "{}"
  });
  const response = await worker.fetch(request, { DB: {}, JWT_SECRET: secret });
  assert.equal(response.status, 401);
  assert.equal((await response.json()).error, "UNAUTHORIZED");
});

test("sync endpoint acknowledges only persisted event IDs", async () => {
  const batch = await makeBatch();
  const db = fakeDb(batch.requestHash, batch.eventHash);
  const token = await signToken("11111111-1111-4111-8111-111111111111");
  const request = new Request("https://example.test/api/v1/sync", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token,
      "X-Sync-Protocol-Version": "1.0",
      "X-Idempotency-Key": batch.batchId
    },
    body: batch.body
  });
  const response = await worker.fetch(request, { DB: db, JWT_SECRET: secret });
  const result = await response.json();
  assert.equal(response.status, 200);
  assert.deepEqual(result.acknowledgedEventIds, [batch.event.eventId]);
  assert.equal(db.batchCalls, 1);
});

function createMockD1() {
  const store = {
    sync_batches: new Map(),
    sync_events: new Map(),
    shares: new Map(),
    social_posts: [],
    user_profiles: new Map(),
    app_releases: []
  };

  function executeStatement(sql, args) {
    const trimmed = sql.trim();
    if (trimmed.startsWith("INSERT INTO sync_batches")) {
      const [userId, idempotencyKey, requestHash, createdAt] = args;
      const key = `${userId}:${idempotencyKey}`;
      if (!store.sync_batches.has(key)) {
        store.sync_batches.set(key, { user_id: userId, idempotency_key: idempotencyKey, request_hash: requestHash, created_at: createdAt });
      }
      return { success: true };
    }
    if (trimmed.startsWith("INSERT OR IGNORE INTO sync_events")) {
      const [userId, eventId, eventType, payload, timestamp, eventHash, idempotencyKey, createdAt, checkUserId, checkKey, checkHash] = args;
      const batchKey = `${checkUserId}:${checkKey}`;
      const batch = store.sync_batches.get(batchKey);
      if (batch && batch.request_hash === checkHash) {
        const evKey = `${userId}:${eventId}`;
        if (!store.sync_events.has(evKey)) {
          store.sync_events.set(evKey, {
            user_id: userId,
            event_id: eventId,
            event_type: eventType,
            payload_json: payload,
            client_timestamp: timestamp,
            event_hash: eventHash,
            idempotency_key: idempotencyKey,
            created_at: createdAt
          });
        }
      }
      return { success: true };
    }
    if (trimmed.startsWith("INSERT INTO shares")) {
      const [code, targetId, targetType, userId, createdAt, expiresAt] = args;
      store.shares.set(code, {
        code,
        target_id: targetId,
        target_type: targetType,
        user_id: userId,
        created_at: createdAt,
        expires_at: expiresAt
      });
      return { success: true };
    }
    if (trimmed.startsWith("INSERT INTO social_posts")) {
      const [id, userId, displayName, avatarUrl, actionText, trackId, trackTitle, trackArtist, createdAt] = args;
      store.social_posts.unshift({
        id,
        user_id: userId,
        user_display_name: displayName,
        avatar_url: avatarUrl,
        action_text: actionText,
        track_id: trackId,
        track_title: trackTitle,
        track_artist: trackArtist,
        created_at: createdAt
      });
      return { success: true };
    }
    if (trimmed.startsWith("INSERT INTO user_profiles")) {
      const [userId, displayName, avatarUrl, musicDna, updatedAt] = args;
      store.user_profiles.set(userId, {
        user_id: userId,
        display_name: displayName,
        avatar_url: avatarUrl,
        music_dna_json: musicDna,
        liked_count: 0,
        followers_count: 0,
        following_count: 0,
        updated_at: updatedAt
      });
      return { success: true };
    }
    return { success: true };
  }

  function createStatement(sql, args = []) {
    return {
      _sql: sql,
      _args: args,
      bind(...newArgs) {
        return createStatement(sql, newArgs);
      },
      _execute() {
        return executeStatement(sql, args);
      },
      async run() {
        return executeStatement(sql, args);
      },
      async first() {
        const trimmed = sql.trim();
        if (trimmed.includes("FROM sync_batches WHERE user_id = ? AND idempotency_key = ?")) {
          const [userId, idempotencyKey] = args;
          const batch = store.sync_batches.get(`${userId}:${idempotencyKey}`);
          return batch ? { request_hash: batch.request_hash } : null;
        }
        if (trimmed.includes("FROM shares WHERE code = ?")) {
          const [code] = args;
          return store.shares.get(code) || null;
        }
        if (trimmed.includes("FROM user_profiles WHERE user_id = ?")) {
          const [userId] = args;
          return store.user_profiles.get(userId) || null;
        }
        if (trimmed.includes("FROM app_releases")) {
          const [platform, channel] = args;
          const release = store.app_releases.find(r => r.platform === platform && r.channel === channel && r.approved === 1);
          return release || null;
        }
        if (trimmed.includes("SELECT count(*) as count FROM sync_events")) {
          return { count: store.sync_events.size };
        }
        if (trimmed.includes("SELECT count(*) as count FROM sync_batches")) {
          return { count: store.sync_batches.size };
        }
        if (trimmed.includes("SELECT count(*) as count FROM shares")) {
          return { count: store.shares.size };
        }
        if (trimmed.includes("SELECT 1 AS ready")) {
          return { ready: 1 };
        }
        return null;
      },
      async all() {
        const trimmed = sql.trim();
        if (trimmed.includes("FROM sync_events WHERE user_id = ? AND event_id IN")) {
          const userId = args[0];
          const eventIds = args.slice(1);
          const results = [];
          for (const eventId of eventIds) {
            const ev = store.sync_events.get(`${userId}:${eventId}`);
            if (ev) results.push({ event_id: ev.event_id, event_hash: ev.event_hash });
          }
          return { results };
        }
        if (trimmed.includes("FROM social_posts")) {
          return { results: [...store.social_posts] };
        }
        return { results: [] };
      }
    };
  }

  const db = {
    store,
    batchCalls: 0,
    async batch(statements) {
      this.batchCalls++;
      for (const stmt of statements) {
        if (stmt._execute) stmt._execute();
      }
      return [];
    },
    prepare(sql) {
      return createStatement(sql);
    }
  };
  return db;
}

test("sync endpoint detects idempotency key reuse with mismatched hash (409 Conflict)", async () => {
  const batch = await makeBatch();
  const db = createMockD1();
  const token = await signToken("11111111-1111-4111-8111-111111111111");

  // First request succeeds
  const req1 = new Request("https://example.test/api/v1/sync", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token,
      "X-Sync-Protocol-Version": "1.0",
      "X-Idempotency-Key": batch.batchId
    },
    body: batch.body
  });
  const res1 = await worker.fetch(req1, { DB: db, JWT_SECRET: secret });
  assert.equal(res1.status, 200);

  // Second request with same idempotency key but different event payload should fail with 409
  const modifiedBody = JSON.parse(batch.body);
  modifiedBody.events[0].payload = JSON.stringify({ songId: "different-song" });
  const req2 = new Request("https://example.test/api/v1/sync", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token,
      "X-Sync-Protocol-Version": "1.0",
      "X-Idempotency-Key": batch.batchId
    },
    body: JSON.stringify(modifiedBody)
  });
  const res2 = await worker.fetch(req2, { DB: db, JWT_SECRET: secret });
  assert.equal(res2.status, 409);
  assert.equal((await res2.json()).error, "IDEMPOTENCY_KEY_REUSED");
});

test("sync endpoint rejects oversized event payload (> 16384 bytes)", async () => {
  const batch = await makeBatch();
  const db = createMockD1();
  const token = await signToken("11111111-1111-4111-8111-111111111111");

  const oversizedBody = JSON.parse(batch.body);
  oversizedBody.events[0].payload = JSON.stringify({ junk: "x".repeat(20000) });
  const req = new Request("https://example.test/api/v1/sync", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token,
      "X-Sync-Protocol-Version": "1.0",
      "X-Idempotency-Key": batch.batchId
    },
    body: JSON.stringify(oversizedBody)
  });
  const res = await worker.fetch(req, { DB: db, JWT_SECRET: secret });
  assert.equal(res.status, 400);
  assert.equal((await res.json()).error, "INVALID_SYNC_EVENT");
});

test("metrics endpoint queries live D1 event, batch, and share counts", async () => {
  const db = createMockD1();
  const batch = await makeBatch();
  const token = await signToken("11111111-1111-4111-8111-111111111111");

  // Ingest 1 sync batch
  await worker.fetch(new Request("https://example.test/api/v1/sync", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token,
      "X-Sync-Protocol-Version": "1.0",
      "X-Idempotency-Key": batch.batchId
    },
    body: batch.body
  }), { DB: db, JWT_SECRET: secret });

  // Query metrics
  const req = new Request("https://example.test/api/v1/metrics");
  const res = await worker.fetch(req, { DB: db });
  assert.equal(res.status, 200);
  const data = await res.json();
  assert.equal(data.status, "ok");
  assert.equal(data.d1Status, "healthy");
  assert.equal(data.metrics.syncEvents, 1);
  assert.equal(data.metrics.syncBatches, 1);
  assert.equal(data.metrics.shares, 0);
});

test("shares endpoint creates, persists to D1, and retrieves short share link", async () => {
  const db = createMockD1();
  const token = await signToken("11111111-1111-4111-8111-111111111111");

  // Create share
  const postReq = new Request("https://example.test/api/v1/shares", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token
    },
    body: JSON.stringify({ targetId: "song_abc123", targetType: "track" })
  });
  const postRes = await worker.fetch(postReq, { DB: db, JWT_SECRET: secret });
  assert.equal(postRes.status, 201);
  const created = await postRes.json();
  assert.equal(created.success, true);
  assert.ok(created.code);
  assert.equal(created.targetId, "song_abc123");

  // Verify stored in mock D1
  assert.ok(db.store.shares.has(created.code));

  // Retrieve share
  const getReq = new Request(`https://example.test/api/v1/shares/${created.code}`);
  const getRes = await worker.fetch(getReq, { DB: db });
  assert.equal(getRes.status, 200);
  const fetched = await getRes.json();
  assert.equal(fetched.success, true);
  assert.equal(fetched.share.code, created.code);
  assert.equal(fetched.share.targetId, "song_abc123");
  assert.equal(fetched.share.targetType, "track");
});

test("shares endpoint returns 404 for unknown share code", async () => {
  const db = createMockD1();
  const req = new Request("https://example.test/api/v1/shares/nonexistent");
  const res = await worker.fetch(req, { DB: db });
  assert.equal(res.status, 404);
  const data = await res.json();
  assert.equal(data.success, false);
  assert.equal(data.error, "SHARE_NOT_FOUND");
});

test("social feed endpoint creates post and fetches persistent feed from D1", async () => {
  const db = createMockD1();
  const token = await signToken("11111111-1111-4111-8111-111111111111");

  // Create post
  const postReq = new Request("https://example.test/api/v1/social/feed", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token
    },
    body: JSON.stringify({
      displayName: "Alice",
      actionText: "is listening to Cyberpunk Synth",
      trackTitle: "Cyberpunk Synth",
      trackArtist: "NØ AI"
    })
  });
  const postRes = await worker.fetch(postReq, { DB: db, JWT_SECRET: secret });
  assert.equal(postRes.status, 201);
  const postData = await postRes.json();
  assert.equal(postData.success, true);
  assert.equal(postData.post.user, "Alice");
  assert.equal(postData.post.action, "is listening to Cyberpunk Synth");

  // Fetch feed
  const getReq = new Request("https://example.test/api/v1/social/feed");
  const getRes = await worker.fetch(getReq, { DB: db });
  assert.equal(getRes.status, 200);
  const feedData = await getRes.json();
  assert.equal(feedData.success, true);
  assert.equal(feedData.feed.length, 1);
  assert.equal(feedData.feed[0].user, "Alice");
  assert.equal(feedData.feed[0].trackTitle, "Cyberpunk Synth");
});

test("user profile endpoint updates profile with Music DNA and retrieves from D1", async () => {
  const db = createMockD1();
  const userId = "11111111-1111-4111-8111-111111111111";
  const token = await signToken(userId);

  // Update profile
  const putReq = new Request(`https://example.test/api/v1/profiles/${userId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token
    },
    body: JSON.stringify({
      displayName: "Cyber Sam",
      musicDna: { synthwave: 85, ambient: 60 }
    })
  });
  const putRes = await worker.fetch(putReq, { DB: db, JWT_SECRET: secret });
  assert.equal(putRes.status, 200);
  const putData = await putRes.json();
  assert.equal(putData.success, true);
  assert.equal(putData.profile.displayName, "Cyber Sam");
  assert.deepEqual(putData.profile.musicDna, { synthwave: 85, ambient: 60 });

  // Get profile
  const getReq = new Request(`https://example.test/api/v1/profiles/${userId}`);
  const getRes = await worker.fetch(getReq, { DB: db });
  assert.equal(getRes.status, 200);
  const getData = await getRes.json();
  assert.equal(getData.success, true);
  assert.equal(getData.profile.displayName, "Cyber Sam");
  assert.deepEqual(getData.profile.musicDna, { synthwave: 85, ambient: 60 });
});

test("releases endpoint queries approved release from D1", async () => {
  const db = createMockD1();
  db.store.app_releases.push({
    version_name: "3.2.0-rc1",
    version_code: 30201,
    platform: "android",
    channel: "stable",
    approved: 1,
    download_url: "https://github.com/MrDSP18/notune/releases/download/v3.2.0/notune.apk",
    sha256: "a1b2c3d4e5f60123456789abcdef0123456789abcdef0123456789abcdef0123",
    release_notes: "NØTUNE 3.2.0 Release Candidate with hardened D1 sync",
    created_at: Date.now()
  });

  const req = new Request("https://example.test/api/v1/releases/latest?platform=android&channel=stable");
  const res = await worker.fetch(req, { DB: db });
  assert.equal(res.status, 200);
  const data = await res.json();
  assert.equal(data.versionName, "3.2.0-rc1");
  assert.equal(data.versionCode, 30201);
  assert.equal(data.approved, true);
  assert.equal(data.sha256, "a1b2c3d4e5f60123456789abcdef0123456789abcdef0123456789abcdef0123");
});
