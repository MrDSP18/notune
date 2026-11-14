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
  btoa,
  fetch,
  setTimeout,
  clearTimeout,
  console
});
const worker = module.exports;
const VALID_SECRET = "a_very_strong_production_signing_secret_for_notune_auth_tests_2026";
const SHORT_SECRET = "short-secret-under-32-chars";
const PLACEHOLDER_SECRET = "notune_production_jwt_secret_key_2026";

function createAuthMockD1() {
  const store = {
    users: new Map(),
    user_credentials: new Map(),
    sessions: new Map(),
    auth_attempts: [],
    user_profiles: new Map()
  };

  function executeStatement(sql, args) {
    const trimmed = sql.trim();

    if (trimmed.startsWith("INSERT INTO users")) {
      const [id, username, email, createdAt] = args;
      store.users.set(id, { id, username, email, created_at: createdAt });
      return { success: true };
    }

    if (trimmed.startsWith("INSERT INTO user_credentials")) {
      const [userId, passwordHash, salt, iterations, algorithm, createdAt, updatedAt] = args;
      store.user_credentials.set(userId, {
        user_id: userId,
        password_hash: passwordHash,
        salt,
        iterations,
        algorithm,
        created_at: createdAt,
        updated_at: updatedAt
      });
      return { success: true };
    }

    if (trimmed.startsWith("INSERT INTO user_profiles")) {
      const [userId, displayName, avatarUrl, musicDna, likedCount, followersCount, followingCount, updatedAt] = args;
      store.user_profiles.set(userId, {
        user_id: userId,
        display_name: displayName,
        avatar_url: avatarUrl,
        music_dna_json: musicDna,
        liked_count: likedCount,
        followers_count: followersCount,
        following_count: followingCount,
        updated_at: updatedAt
      });
      return { success: true };
    }

    if (trimmed.startsWith("INSERT INTO sessions")) {
      const [id, userId, deviceId, refreshTokenHash, isRevoked, expiresAt, createdAt, lastActiveAt] = args;
      store.sessions.set(id, {
        id,
        user_id: userId,
        device_id: deviceId,
        refresh_token_hash: refreshTokenHash,
        is_revoked: isRevoked,
        expires_at: expiresAt,
        created_at: createdAt,
        last_active_at: lastActiveAt
      });
      return { success: true };
    }

    if (trimmed.startsWith("INSERT INTO auth_attempts")) {
      const [id, identifier, ipAddress, success, attemptedAt] = args;
      store.auth_attempts.push({
        id,
        identifier: identifier.toLowerCase(),
        ip_address: ipAddress,
        success,
        attempted_at: attemptedAt
      });
      return { success: true };
    }

    if (trimmed.startsWith("UPDATE sessions SET is_revoked = 1 WHERE id = ?")) {
      const [id] = args;
      const session = store.sessions.get(id);
      if (session) session.is_revoked = 1;
      return { success: true };
    }

    if (trimmed.startsWith("UPDATE sessions SET is_revoked = 1 WHERE refresh_token_hash = ?")) {
      const [hash] = args;
      for (const s of store.sessions.values()) {
        if (s.refresh_token_hash === hash) {
          s.is_revoked = 1;
        }
      }
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

        if (trimmed.includes("FROM users WHERE LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?)") ||
            trimmed.includes("FROM users WHERE username = ? OR email = ?")) {
          const [u, e] = args;
          for (const user of store.users.values()) {
            if ((user.username && user.username.toLowerCase() === (u || "").toLowerCase()) ||
                (user.email && user.email.toLowerCase() === (e || "").toLowerCase())) {
              return { ...user };
            }
          }
          return null;
        }

        if (trimmed.includes("FROM users WHERE LOWER(email) = LOWER(?) OR LOWER(username) = LOWER(?)")) {
          const [ident] = args;
          const search = (ident || "").toLowerCase();
          for (const user of store.users.values()) {
            if ((user.email && user.email.toLowerCase() === search) ||
                (user.username && user.username.toLowerCase() === search)) {
              return { ...user };
            }
          }
          return null;
        }

        if (trimmed.includes("FROM user_credentials WHERE user_id = ?")) {
          const [userId] = args;
          const creds = store.user_credentials.get(userId);
          return creds ? { ...creds } : null;
        }

        if (trimmed.includes("FROM sessions WHERE refresh_token_hash = ?")) {
          const [hash] = args;
          for (const s of store.sessions.values()) {
            if (s.refresh_token_hash === hash) {
              return { ...s };
            }
          }
          return null;
        }

        if (trimmed.includes("FROM users WHERE id = ?")) {
          const [id] = args;
          const user = store.users.get(id);
          return user ? { ...user } : null;
        }

        if (trimmed.includes("FROM users u LEFT JOIN user_profiles p ON u.id = p.user_id WHERE u.id = ?")) {
          const [id] = args;
          const user = store.users.get(id);
          if (!user) return null;
          const profile = store.user_profiles.get(id) || {};
          return {
            id: user.id,
            username: user.username,
            email: user.email,
            created_at: user.created_at,
            display_name: profile.display_name || user.username,
            avatar_url: profile.avatar_url || null
          };
        }

        if (trimmed.includes("SELECT COUNT(*) as failedCount FROM auth_attempts")) {
          const [ident, ip, cutoff] = args;
          const search = (ident || "").toLowerCase();
          const count = store.auth_attempts.filter(a =>
            (a.identifier === search || a.ip_address === ip) &&
            a.success === 0 &&
            a.attempted_at >= cutoff
          ).length;
          return { failedCount: count };
        }

        return null;
      },
      async all() {
        return { results: [] };
      }
    };
  }

  const db = {
    store,
    async batch(statements) {
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

// --------------------------------------------------------------------------
// 1. Service Availability & Fail-Closed Secret Validation
// --------------------------------------------------------------------------

test("register endpoint rejects requests when JWT_SECRET is missing or invalid (503)", async () => {
  const db = createAuthMockD1();
  const req = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: "alice", email: "alice@test.com", password: "Password123!" })
  });

  // Missing secret
  const res1 = await worker.fetch(req.clone(), { DB: db });
  assert.equal(res1.status, 503);

  // Short secret
  const res2 = await worker.fetch(req.clone(), { DB: db, JWT_SECRET: SHORT_SECRET });
  assert.equal(res2.status, 503);

  // Placeholder secret
  const res3 = await worker.fetch(req.clone(), { DB: db, JWT_SECRET: PLACEHOLDER_SECRET });
  assert.equal(res3.status, 503);
});

// --------------------------------------------------------------------------
// 2. User Registration (Validation & Cryptographic Storage)
// --------------------------------------------------------------------------

test("register endpoint validates fields: username, email, and password", async () => {
  const db = createAuthMockD1();

  // Invalid username (too short)
  const reqShortUsername = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: "al", email: "al@test.com", password: "Password123!" })
  });
  const resShortUsername = await worker.fetch(reqShortUsername, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(resShortUsername.status, 400);
  assert.equal((await resShortUsername.json()).error, "INVALID_USERNAME");

  // Invalid email format
  const reqBadEmail = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: "alice", email: "not-an-email", password: "Password123!" })
  });
  const resBadEmail = await worker.fetch(reqBadEmail, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(resBadEmail.status, 400);
  assert.equal((await resBadEmail.json()).error, "INVALID_EMAIL");

  // Password too short (< 8 chars)
  const reqShortPassword = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: "alice", email: "alice@test.com", password: "short" })
  });
  const resShortPassword = await worker.fetch(reqShortPassword, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(resShortPassword.status, 400);
  assert.equal((await resShortPassword.json()).error, "INVALID_PASSWORD");
});

test("register endpoint successfully registers user with PBKDF2 salt & hash, and issues tokens", async () => {
  const db = createAuthMockD1();
  const req = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "alice_w",
      email: "alice@notune.app",
      password: "StrongPassword2026!",
      displayName: "Alice Wonderland"
    })
  });

  const res = await worker.fetch(req, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(res.status, 201);
  const data = await res.json();

  assert.equal(data.success, true);
  assert.equal(data.user.username, "alice_w");
  assert.equal(data.user.email, "alice@notune.app");
  assert.equal(data.user.displayName, "Alice Wonderland");
  assert.ok(data.tokens.accessToken);
  assert.ok(data.tokens.refreshToken);
  assert.equal(data.tokens.expiresIn, 3600);

  // Check stored database records
  const userId = data.user.id;
  const user = db.store.users.get(userId);
  assert.ok(user);
  assert.equal(user.username, "alice_w");

  const creds = db.store.user_credentials.get(userId);
  assert.ok(creds);
  assert.equal(creds.algorithm, "PBKDF2-SHA256");
  assert.equal(creds.iterations, 100000);
  assert.ok(creds.salt);
  assert.equal(creds.salt.length, 32); // 16 bytes hex = 32 chars
  assert.ok(creds.password_hash);
  assert.equal(creds.password_hash.length, 64); // 32 bytes hex = 64 chars
  // Password itself is NEVER stored
  assert.notEqual(creds.password_hash, "StrongPassword2026!");
});

test("register endpoint enforces username and email uniqueness (409 Conflict)", async () => {
  const db = createAuthMockD1();
  const regPayload = {
    username: "unique_user",
    email: "unique@notune.app",
    password: "Password123!"
  };

  const req1 = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(regPayload)
  });
  const res1 = await worker.fetch(req1, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(res1.status, 201);

  // Duplicate username
  const reqDupUser = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "unique_user",
      email: "different@notune.app",
      password: "Password123!"
    })
  });
  const resDupUser = await worker.fetch(reqDupUser, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(resDupUser.status, 409);
  assert.equal((await resDupUser.json()).error, "USERNAME_TAKEN");

  // Duplicate email
  const reqDupEmail = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "different_user",
      email: "unique@notune.app",
      password: "Password123!"
    })
  });
  const resDupEmail = await worker.fetch(reqDupEmail, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(resDupEmail.status, 409);
  assert.equal((await resDupEmail.json()).error, "EMAIL_TAKEN");
});

// --------------------------------------------------------------------------
// 3. User Login & Password Verification
// --------------------------------------------------------------------------

test("login endpoint requires identifier and password", async () => {
  const db = createAuthMockD1();

  const reqMissingId = new Request("https://example.test/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ password: "some_password" })
  });
  const resMissingId = await worker.fetch(reqMissingId, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(resMissingId.status, 400);
  assert.equal((await resMissingId.json()).error, "IDENTIFIER_REQUIRED");

  const reqMissingPass = new Request("https://example.test/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ identifier: "alice@test.com" })
  });
  const resMissingPass = await worker.fetch(reqMissingPass, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(resMissingPass.status, 400);
  assert.equal((await resMissingPass.json()).error, "PASSWORD_REQUIRED");
});

test("login endpoint strictly rejects incorrect passwords with generic 401 INVALID_CREDENTIALS", async () => {
  const db = createAuthMockD1();

  // Register user first
  const regReq = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "bob_listener",
      email: "bob@notune.app",
      password: "CorrectSecretPassword123!"
    })
  });
  await worker.fetch(regReq, { DB: db, JWT_SECRET: VALID_SECRET });

  // Attempt login with WRONG password
  const loginWrongPassReq = new Request("https://example.test/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      identifier: "bob@notune.app",
      password: "WrongPassword123!"
    })
  });
  const loginWrongPassRes = await worker.fetch(loginWrongPassReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(loginWrongPassRes.status, 401);
  const errBody = await loginWrongPassRes.json();
  assert.equal(errBody.error, "INVALID_CREDENTIALS");

  // Attempt login with unknown user
  const loginUnknownReq = new Request("https://example.test/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      identifier: "unknown_person@notune.app",
      password: "AnyPassword123!"
    })
  });
  const loginUnknownRes = await worker.fetch(loginUnknownReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(loginUnknownRes.status, 401);
  assert.equal((await loginUnknownRes.json()).error, "INVALID_CREDENTIALS");
});

test("login endpoint succeeds with correct email or username and password", async () => {
  const db = createAuthMockD1();

  // Register user
  const regReq = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "charlie_musician",
      email: "charlie@notune.app",
      password: "MyValidSecretPassword2026!"
    })
  });
  await worker.fetch(regReq, { DB: db, JWT_SECRET: VALID_SECRET });

  // 1. Login with email
  const loginEmailReq = new Request("https://example.test/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      identifier: "charlie@notune.app",
      password: "MyValidSecretPassword2026!"
    })
  });
  const loginEmailRes = await worker.fetch(loginEmailReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(loginEmailRes.status, 200);
  const emailData = await loginEmailRes.json();
  assert.equal(emailData.success, true);
  assert.equal(emailData.user.username, "charlie_musician");
  assert.ok(emailData.tokens.accessToken);
  assert.ok(emailData.tokens.refreshToken);

  // 2. Login with username
  const loginUserReq = new Request("https://example.test/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      identifier: "charlie_musician",
      password: "MyValidSecretPassword2026!"
    })
  });
  const loginUserRes = await worker.fetch(loginUserReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(loginUserRes.status, 200);
  const userData = await loginUserRes.json();
  assert.equal(userData.success, true);
  assert.equal(userData.user.email, "charlie@notune.app");
});

test("login endpoint enforces brute-force throttling after 5 failed attempts (429)", async () => {
  const db = createAuthMockD1();

  // Register user
  const regReq = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "target_account",
      email: "target@notune.app",
      password: "TargetPassword123!"
    })
  });
  await worker.fetch(regReq, { DB: db, JWT_SECRET: VALID_SECRET });

  // Make 5 consecutive failed login attempts
  for (let i = 0; i < 5; i++) {
    const failReq = new Request("https://example.test/api/v1/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json", "CF-Connecting-IP": "198.51.100.1" },
      body: JSON.stringify({ identifier: "target@notune.app", password: `WrongAttempt_${i}` })
    });
    const failRes = await worker.fetch(failReq, { DB: db, JWT_SECRET: VALID_SECRET });
    assert.equal(failRes.status, 401);
  }

  // 6th attempt should be blocked with 429 Too Many Requests
  const throttledReq = new Request("https://example.test/api/v1/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json", "CF-Connecting-IP": "198.51.100.1" },
    body: JSON.stringify({ identifier: "target@notune.app", password: "TargetPassword123!" })
  });
  const throttledRes = await worker.fetch(throttledReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(throttledRes.status, 429);
  assert.equal((await throttledRes.json()).error, "TOO_MANY_ATTEMPTS");
});

// --------------------------------------------------------------------------
// 4. Authenticated Identity & Token Refresh/Revocation
// --------------------------------------------------------------------------

test("/api/v1/auth/me returns profile for valid JWT and rejects unauthorized calls", async () => {
  const db = createAuthMockD1();

  // Register user
  const regReq = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "diana_sound",
      email: "diana@notune.app",
      password: "DianaSecurePassword2026!"
    })
  });
  const regRes = await worker.fetch(regReq, { DB: db, JWT_SECRET: VALID_SECRET });
  const { tokens } = await regRes.json();

  // Call /me without token -> 401
  const noTokenReq = new Request("https://example.test/api/v1/auth/me", { method: "GET" });
  const noTokenRes = await worker.fetch(noTokenReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(noTokenRes.status, 401);

  // Call /me with forged token -> 401
  const forgedTokenReq = new Request("https://example.test/api/v1/auth/me", {
    method: "GET",
    headers: { "Authorization": "Bearer forged.token.value" }
  });
  const forgedTokenRes = await worker.fetch(forgedTokenReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(forgedTokenRes.status, 401);

  // Call /me with valid token -> 200
  const validTokenReq = new Request("https://example.test/api/v1/auth/me", {
    method: "GET",
    headers: { "Authorization": `Bearer ${tokens.accessToken}` }
  });
  const validTokenRes = await worker.fetch(validTokenReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(validTokenRes.status, 200);
  const meData = await validTokenRes.json();
  assert.equal(meData.success, true);
  assert.equal(meData.user.username, "diana_sound");
  assert.equal(meData.user.email, "diana@notune.app");
});

test("refresh endpoint rotates tokens and rejects revoked tokens", async () => {
  const db = createAuthMockD1();

  const regReq = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "elena_wave",
      email: "elena@notune.app",
      password: "ElenaPassword2026!"
    })
  });
  const regRes = await worker.fetch(regReq, { DB: db, JWT_SECRET: VALID_SECRET });
  const { tokens } = await regRes.json();
  const initialRefreshToken = tokens.refreshToken;

  // Refresh token
  const refreshReq = new Request("https://example.test/api/v1/auth/refresh", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken: initialRefreshToken })
  });
  const refreshRes = await worker.fetch(refreshReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(refreshRes.status, 200);
  const refreshData = await refreshRes.json();
  assert.ok(refreshData.tokens.accessToken);
  assert.ok(refreshData.tokens.refreshToken);
  assert.notEqual(refreshData.tokens.refreshToken, initialRefreshToken);

  // Re-use of old refresh token must be rejected (revoked on rotation)
  const reuseReq = new Request("https://example.test/api/v1/auth/refresh", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken: initialRefreshToken })
  });
  const reuseRes = await worker.fetch(reuseReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(reuseRes.status, 401);
  assert.equal((await reuseRes.json()).error, "INVALID_REFRESH_TOKEN");
});

test("logout endpoint revokes refresh token", async () => {
  const db = createAuthMockD1();

  const regReq = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "frank_beats",
      email: "frank@notune.app",
      password: "FrankPassword2026!"
    })
  });
  const regRes = await worker.fetch(regReq, { DB: db, JWT_SECRET: VALID_SECRET });
  const { tokens } = await regRes.json();

  // Logout
  const logoutReq = new Request("https://example.test/api/v1/auth/logout", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken: tokens.refreshToken })
  });
  const logoutRes = await worker.fetch(logoutReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(logoutRes.status, 200);

  // Subsequent refresh should fail
  const postLogoutRefreshReq = new Request("https://example.test/api/v1/auth/refresh", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken: tokens.refreshToken })
  });
  const postLogoutRefreshRes = await worker.fetch(postLogoutRefreshReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(postLogoutRefreshRes.status, 401);
  assert.equal((await postLogoutRefreshRes.json()).error, "INVALID_REFRESH_TOKEN");
});

test("access tokens issued by login authenticate with /api/v1/sync", async () => {
  const db = createAuthMockD1();

  // Add support for sync tables in mock DB
  db.store.sync_batches = new Map();
  db.store.sync_events = new Map();

  const origPrepare = db.prepare.bind(db);
  db.prepare = (sql) => {
    const trimmed = sql.trim();
    if (trimmed.startsWith("INSERT INTO sync_batches")) {
      return {
        bind(...args) {
          const [userId, idempotencyKey, requestHash, createdAt] = args;
          db.store.sync_batches.set(`${userId}:${idempotencyKey}`, { request_hash: requestHash });
          return { async run() { return { success: true }; } };
        }
      };
    }
    if (trimmed.startsWith("INSERT OR IGNORE INTO sync_events")) {
      return {
        bind(...args) {
          const [userId, eventId, eventType, payload, timestamp, eventHash, idempotencyKey, createdAt, checkUserId, checkKey, checkHash] = args;
          db.store.sync_events.set(`${userId}:${eventId}`, { event_id: eventId, event_hash: eventHash });
          return { async run() { return { success: true }; } };
        }
      };
    }
    if (trimmed.includes("FROM sync_batches WHERE user_id = ? AND idempotency_key = ?")) {
      return {
        bind(...args) {
          const [userId, idempotencyKey] = args;
          const b = db.store.sync_batches.get(`${userId}:${idempotencyKey}`);
          return { async first() { return b || null; } };
        }
      };
    }
    if (trimmed.includes("FROM sync_events WHERE user_id = ? AND event_id IN")) {
      return {
        bind(...args) {
          const userId = args[0];
          const eventIds = args.slice(1);
          const results = eventIds.map(id => db.store.sync_events.get(`${userId}:${id}`)).filter(Boolean);
          return { async all() { return { results }; } };
        }
      };
    }
    return origPrepare(sql);
  };

  // Register and login
  const regReq = new Request("https://example.test/api/v1/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: "grace_sync",
      email: "grace@notune.app",
      password: "GracePassword2026!"
    })
  });
  const regRes = await worker.fetch(regReq, { DB: db, JWT_SECRET: VALID_SECRET });
  const { tokens } = await regRes.json();

  // Create valid sync batch
  const event = {
    eventId: "44444444-4444-4444-8444-444444444444",
    eventType: "PLAYBACK_HISTORY_LOGGED",
    payload: JSON.stringify({ songId: "track_1" }),
    timestamp: Date.now()
  };
  const batchId = "55555555-5555-4555-8555-555555555555";
  const syncBody = JSON.stringify({
    batchId,
    protocolVersion: "1.0",
    eventsCount: 1,
    events: [event],
    clientTimestamp: Date.now()
  });

  const syncReq = new Request("https://example.test/api/v1/sync", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${tokens.accessToken}`,
      "X-Sync-Protocol-Version": "1.0",
      "X-Idempotency-Key": batchId
    },
    body: syncBody
  });

  const syncRes = await worker.fetch(syncReq, { DB: db, JWT_SECRET: VALID_SECRET });
  assert.equal(syncRes.status, 200);
  const syncData = await syncRes.json();
  assert.deepEqual(syncData.acknowledgedEventIds, [event.eventId]);
});
