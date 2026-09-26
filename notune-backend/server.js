/**
 * NØTUNE Cloud & Social Ecosystem Backend Server Service
 * Production-grade REST API + WebSocket Server with PostgreSQL Persistence.
 * 
 * Scalability Target: Designed to scale toward 100,000 concurrent connections,
 * subject to load testing, cloud-provider quotas, networking limits, database capacity, and workload characteristics.
 */

const express = require('express');
const http = require('http');
const WebSocket = require('ws');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const { Pool } = require('pg');
const rateLimit = require('express-rate-limit');
const helmet = require('helmet');
const crypto = require('crypto');
require('dotenv').config();

const PORT = process.env.PORT || 10000;
const JWT_SECRET = process.env.JWT_SECRET || 'notune_production_jwt_secret_key_2026';
const DATABASE_URL = process.env.DATABASE_URL || 'postgresql://postgres:postgres@localhost:5432/notune_db';

const app = express();

// Security & Middleware
app.use(helmet({ contentSecurityPolicy: false }));
app.use(cors());
app.use(express.json({ limit: '1mb' }));

// Global Rate Limiting
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 1000,
  standardHeaders: true,
  legacyHeaders: false,
  message: { error: 'Too many requests, please try again later.' }
});
app.use('/api/', apiLimiter);

// Bounded PostgreSQL Connection Pool
const pool = new Pool({
  connectionString: DATABASE_URL,
  max: parseInt(process.env.DB_POOL_MAX || '25', 10),
  idleTimeoutMillis: 30000,
  connectionTimeoutMillis: 5000,
  ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false
});

pool.on('error', (err) => {
  console.error('Unexpected PostgreSQL Pool Error:', err.message);
});

// Middleware: Authentication Token Extraction
function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) {
    req.user = { id: '00000000-0000-0000-0000-000000000000', username: 'guest_user' };
    return next();
  }
  jwt.verify(token, JWT_SECRET, (err, user) => {
    if (err) return res.status(403).json({ error: 'Invalid or expired session token' });
    req.user = user;
    next();
  });
}

// --------------------------------------------------------------------
// Health & Readiness Probes
// --------------------------------------------------------------------

// Liveness Probe: Fast 200 OK without DB overhead
app.get('/health', (req, res) => {
  res.json({
    status: 'UP',
    service: 'NØTUNE Cloud Server',
    version: '2.0.0',
    timestamp: new Date().toISOString()
  });
});

app.get('/api/v1/health', (req, res) => {
  res.json({ status: 'UP', service: 'NØTUNE Cloud Server', timestamp: new Date().toISOString() });
});

// Readiness Probe: Verifies database connectivity
app.get('/ready', async (req, res) => {
  try {
    const dbRes = await pool.query('SELECT 1 as ready');
    if (dbRes.rows[0].ready === 1) {
      return res.json({ status: 'READY', db: 'CONNECTED', timestamp: new Date().toISOString() });
    }
    res.status(503).json({ status: 'UNREADY', db: 'UNAVAILABLE' });
  } catch (err) {
    res.status(503).json({ status: 'UNREADY', error: err.message });
  }
});

// --------------------------------------------------------------------
// REST API Endpoints (Metadata, Auth, Social - Zero Audio Bytes)
// --------------------------------------------------------------------

app.post('/api/v1/auth/register', async (req, res) => {
  const { email, displayName, avatarUrl } = req.body;
  if (!displayName) return res.status(400).json({ error: 'displayName is required' });

  try {
    const query = `
      INSERT INTO users (email, display_name, avatar_url)
      VALUES ($1, $2, $3)
      RETURNING id, display_name, avatar_url, created_at;
    `;
    const result = await pool.query(query, [email || null, displayName, avatarUrl || null]);
    const user = result.rows[0];
    const token = jwt.sign({ id: user.id, username: user.display_name }, JWT_SECRET, { expiresIn: '30d' });
    res.json({ token, user });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/v1/friends', authenticateToken, async (req, res) => {
  try {
    const query = `
      SELECT f.id as friendship_id, u.id, u.display_name, u.avatar_url, p.status as presence_status, p.current_track_title, p.current_artist_name
      FROM friendships f
      JOIN users u ON (u.id = CASE WHEN f.requester_id = $1 THEN f.addressee_id ELSE f.requester_id END)
      LEFT JOIN user_presence p ON p.user_id = u.id
      WHERE (f.requester_id = $1 OR f.addressee_id = $1) AND f.status = 'ACCEPTED';
    `;
    const result = await pool.query(query, [req.user.id]);
    res.json({ friends: result.rows });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/v1/feed', authenticateToken, async (req, res) => {
  try {
    const query = `
      SELECT p.*, u.display_name as author_name, u.avatar_url as author_avatar_url
      FROM social_posts p
      JOIN users u ON u.id = p.author_id
      WHERE p.visibility = 'PUBLIC'
      ORDER BY p.created_at DESC
      LIMIT 50;
    `;
    const result = await pool.query(query);
    res.json({ posts: result.rows });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/v1/posts', authenticateToken, async (req, res) => {
  const { postType, songId, songTitle, artistName, caption, momentTimestamp, moodEmoji } = req.body;
  try {
    const query = `
      INSERT INTO social_posts (author_id, post_type, song_id, song_title, artist_name, caption, moment_timestamp, mood_emoji)
      VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
      RETURNING *;
    `;
    const result = await pool.query(query, [
      req.user.id,
      postType || 'SONG',
      songId || null,
      songTitle || null,
      artistName || null,
      caption || '',
      momentTimestamp || null,
      moodEmoji || null
    ]);
    res.json({ post: result.rows[0] });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// --------------------------------------------------------------------
// Real-Time Room & Presence Synchronization (WebSocket)
// --------------------------------------------------------------------

const server = http.createServer(app);
const wss = new WebSocket.Server({ server, maxPayload: 128 * 1024 });

// In-Memory Ephemeral State (Zero Database Write for Heartbeats)
const connectedClients = new Map(); // userId -> { ws, username, currentRoomId }
const roomsState = new Map(); // roomId -> { roomCode, hostUserId, sequence: number, members: Set<userId>, queue: Array }
const ephemeralPresence = new Map(); // userId -> { status, trackData, lastActiveTimestamp }

wss.on('connection', (ws, req) => {
  let userId = null;
  let username = 'Anonymous';

  ws.on('message', async (messageBuffer) => {
    try {
      const data = JSON.parse(messageBuffer.toString());
      const { type, roomId, payload } = data;

      switch (type) {
        case 'AUTHENTICATE': {
          userId = data.userId || crypto.randomUUID();
          username = data.username || 'Listener';
          connectedClients.set(userId, { ws, username, currentRoomId: null });
          ephemeralPresence.set(userId, { status: 'ONLINE', trackData: null, lastActiveTimestamp: Date.now() });
          ws.send(JSON.stringify({ type: 'AUTHENTICATED', userId, status: 'SUCCESS' }));
          break;
        }

        case 'HEARTBEAT_PRESENCE': {
          if (userId) {
            ephemeralPresence.set(userId, {
              status: data.status || 'ONLINE',
              trackData: data.currentTrack || null,
              lastActiveTimestamp: Date.now()
            });
            ws.send(JSON.stringify({ type: 'PONG', serverTime: Date.now() }));
          }
          break;
        }

        case 'JOIN_ROOM': {
          if (!userId) break;
          const targetRoomId = roomId || data.roomCode;
          let room = roomsState.get(targetRoomId);

          if (!room) {
            room = {
              roomId: targetRoomId,
              roomCode: targetRoomId,
              hostUserId: userId,
              sequence: 1,
              members: new Set(),
              queue: []
            };
            roomsState.set(targetRoomId, room);
          }

          room.members.add(userId);
          const clientSession = connectedClients.get(userId);
          if (clientSession) clientSession.currentRoomId = targetRoomId;

          broadcastToRoom(targetRoomId, {
            eventId: crypto.randomUUID(),
            roomId: targetRoomId,
            sequence: ++room.sequence,
            type: 'USER_JOINED',
            senderId: userId,
            serverTime: Date.now(),
            payload: { userId, username }
          });
          break;
        }

        case 'ROOM_SYNC_EVENT':
        case 'PLAYBACK_ACTION': {
          if (!userId || !roomId) break;
          const room = roomsState.get(roomId);
          if (!room || !room.members.has(userId)) {
            ws.send(JSON.stringify({ type: 'ERROR', message: 'Unauthorized room action' }));
            break;
          }

          const sequence = ++room.sequence;
          const syncEvent = {
            eventId: crypto.randomUUID(),
            roomId,
            sequence,
            type: payload?.action || type,
            senderId: userId,
            serverTime: Date.now(),
            payload: payload || {}
          };

          broadcastToRoom(roomId, syncEvent);
          break;
        }

        case 'LEAVE_ROOM': {
          if (!userId || !roomId) break;
          const room = roomsState.get(roomId);
          if (room) {
            room.members.delete(userId);
            broadcastToRoom(roomId, {
              eventId: crypto.randomUUID(),
              roomId,
              sequence: ++room.sequence,
              type: 'USER_LEFT',
              senderId: userId,
              serverTime: Date.now(),
              payload: { userId, username }
            });
          }
          break;
        }
      }
    } catch (err) {
      console.error('WS Error:', err.message);
    }
  });

  ws.on('close', () => {
    if (userId) {
      const clientSession = connectedClients.get(userId);
      if (clientSession && clientSession.currentRoomId) {
        const room = roomsState.get(clientSession.currentRoomId);
        if (room) {
          room.members.delete(userId);
          broadcastToRoom(clientSession.currentRoomId, {
            eventId: crypto.randomUUID(),
            roomId: clientSession.currentRoomId,
            sequence: ++room.sequence,
            type: 'USER_LEFT',
            senderId: userId,
            serverTime: Date.now(),
            payload: { userId, username }
          });
        }
      }
      connectedClients.delete(userId);
      ephemeralPresence.delete(userId);
    }
  });
});

function broadcastToRoom(roomId, syncEvent) {
  const room = roomsState.get(roomId);
  if (!room) return;
  const eventString = JSON.stringify(syncEvent);

  for (const memberUserId of room.members) {
    const client = connectedClients.get(memberUserId);
    if (client && client.ws.readyState === WebSocket.OPEN) {
      client.ws.send(eventString);
    }
  }
}

// Start Server
server.listen(PORT, '0.0.0.0', () => {
  console.log(`🚀 NØTUNE Hardened Backend Server running on port ${PORT}`);
});
