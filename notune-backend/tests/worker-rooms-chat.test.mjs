import test from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import vm from "node:vm";
import { webcrypto } from "node:crypto";
import { TextEncoder, TextDecoder } from "node:util";

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

function createRoomMockD1() {
  const store = {
    rooms: new Map(),
    room_members: new Map(),
    room_messages: []
  };

  function executeStatement(sql, args) {
    const trimmed = sql.trim();

    if (trimmed.startsWith("INSERT INTO rooms")) {
      const [id, roomCode, name, roomType, hostId, hostUsername, maxMembers, isActive, createdAt, updatedAt] = args;
      store.rooms.set(roomCode, {
        id, room_code: roomCode, name, room_type: roomType, host_id: hostId,
        host_username: hostUsername, max_members: maxMembers, is_active: isActive,
        created_at: createdAt, updated_at: updatedAt
      });
      return { success: true };
    }

    if (trimmed.startsWith("INSERT INTO room_members")) {
      const [roomId, userId, username, role, isConnected, joinedAt, lastSeenAt] = args;
      store.room_members.set(`${roomId}:${userId}`, {
        room_id: roomId, user_id: userId, username, role, is_connected: isConnected,
        joined_at: joinedAt, last_seen_at: lastSeenAt
      });
      return { success: true };
    }

    if (trimmed.startsWith("INSERT INTO room_messages")) {
      const [id, roomId, userId, username, content, messageType, createdAt] = args;
      store.room_messages.push({
        id, room_id: roomId, user_id: userId, username, content,
        message_type: messageType, created_at: createdAt
      });
      return { success: true };
    }

    return { success: true };
  }

  return {
    store,
    prepare(sql) {
      let boundArgs = [];
      return {
        bind(...args) {
          boundArgs = args;
          return this;
        },
        async run() {
          return executeStatement(sql, boundArgs);
        },
        async first() {
          const trimmed = sql.trim();
          if (trimmed.startsWith("SELECT * FROM rooms WHERE room_code = ?")) {
            return store.rooms.get(boundArgs[0]) || null;
          }
          if (trimmed.startsWith("SELECT id FROM rooms WHERE room_code = ?")) {
            const room = store.rooms.get(boundArgs[0]);
            return room ? { id: room.id } : null;
          }
          return null;
        },
        async all() {
          const trimmed = sql.trim();
          if (trimmed.includes("FROM rooms WHERE is_active = 1")) {
            const rooms = Array.from(store.rooms.values());
            if (boundArgs[0]) {
              return { results: rooms.filter(r => r.room_type === boundArgs[0]) };
            }
            return { results: rooms };
          }
          if (trimmed.includes("FROM room_members WHERE room_id = ?")) {
            const members = Array.from(store.room_members.values()).filter(m => m.room_id === boundArgs[0]);
            return { results: members };
          }
          if (trimmed.includes("FROM room_messages WHERE room_id = ?")) {
            const msgs = store.room_messages.filter(m => m.room_id === boundArgs[0]);
            return { results: msgs };
          }
          return { results: [] };
        }
      };
    }
  };
}

test("room creation supports COUPLE room type with max 2 members", async () => {
  const db = createRoomMockD1();
  const req = new Request("https://notune.app/api/v1/rooms", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      name: "Late Night Couple Vibes",
      roomType: "COUPLE",
      hostUsername: "Dharan"
    })
  });

  const res = await worker.fetch(req, { DB: db });
  assert.equal(res.status, 201);
  const data = await res.json();
  assert.equal(data.success, true);
  assert.equal(data.room.roomType, "COUPLE");
  assert.equal(data.room.maxMembers, 2);
  assert.ok(data.room.roomCode.length === 6);
  assert.equal(db.store.rooms.get(data.room.roomCode).room_type, "COUPLE");
});

test("room creation supports FRIENDS and TEAM room types", async () => {
  const db = createRoomMockD1();
  
  // Friends room
  const req1 = new Request("https://notune.app/api/v1/rooms", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      name: "Weekend Squad",
      roomType: "FRIENDS",
      hostUsername: "Aditya"
    })
  });
  const res1 = await worker.fetch(req1, { DB: db });
  const data1 = await res1.json();
  assert.equal(data1.room.roomType, "FRIENDS");
  assert.equal(data1.room.maxMembers, 10);

  // Team room
  const req2 = new Request("https://notune.app/api/v1/rooms", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      name: "Dev Team Radio",
      roomType: "TEAM",
      hostUsername: "DSP"
    })
  });
  const res2 = await worker.fetch(req2, { DB: db });
  const data2 = await res2.json();
  assert.equal(data2.room.roomType, "TEAM");
  assert.equal(data2.room.maxMembers, 50);
});

test("posting and retrieving room chat messages", async () => {
  const db = createRoomMockD1();
  // Create room first
  const createReq = new Request("https://notune.app/api/v1/rooms", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: "Music Hub", roomType: "FRIENDS", hostUsername: "HostUser" })
  });
  const createRes = await worker.fetch(createReq, { DB: db });
  const { room } = await createRes.json();
  const roomCode = room.roomCode;

  // Send message
  const msgReq = new Request(`https://notune.app/api/v1/rooms/${roomCode}/messages`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      content: "This song is pure fire! 🔥",
      username: "MusicFan"
    })
  });
  const msgRes = await worker.fetch(msgReq, { DB: db });
  assert.equal(msgRes.status, 201);
  const msgData = await msgRes.json();
  assert.equal(msgData.success, true);
  assert.equal(msgData.message.content, "This song is pure fire! 🔥");

  // Get messages
  const getReq = new Request(`https://notune.app/api/v1/rooms/${roomCode}/messages`, {
    method: "GET"
  });
  const getRes = await worker.fetch(getReq, { DB: db });
  assert.equal(getRes.status, 200);
  const getData = await getRes.json();
  assert.equal(getData.messages.length, 1);
  assert.equal(getData.messages[0].content, "This song is pure fire! 🔥");
});

test("websocket endpoint responds to upgrade checks", async () => {
  const req = new Request("https://notune.app/ws", {
    method: "GET"
  });
  const res = await worker.fetch(req, {});
  assert.equal(res.status, 200);
  const data = await res.json();
  assert.equal(data.service, "notune-listen-together-gateway");
});
