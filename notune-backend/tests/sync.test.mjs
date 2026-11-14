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
