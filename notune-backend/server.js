/**
 * NØTUNE Cloud & Social Ecosystem Backend Server Service
 * Production-grade REST API + WebSocket Server with PostgreSQL Persistence.
 */

const express = require('express');
const http = require('http');
const WebSocket = require('ws');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const { Pool } = require('pg');
require('dotenv').config();

const PORT = process.env.PORT || 10000;
const JWT_SECRET = process.env.JWT_SECRET || 'notune_production_jwt_secret_key_2026';
const DATABASE_URL = process.env.DATABASE_URL || 'postgresql://postgres:postgres@localhost:5432/notune_db';

// Cloudflare R2 Object Storage Config (10GB Free S3 Media Storage)
const R2_ACCOUNT_ID = process.env.R2_ACCOUNT_ID || 'c4062f2f7ab8860317a18a059f9cd756';
const R2_ACCESS_KEY_ID = process.env.R2_ACCESS_KEY_ID || '1cde81840d6a160f7a4bbf0230f7c6af';
const R2_SECRET_ACCESS_KEY = process.env.R2_SECRET_ACCESS_KEY || '89606808b1f0074082e844763594283dc3678c9d068ef00c7bab612facc7a97c';
const R2_ENDPOINT = process.env.R2_ENDPOINT || 'https://c4062f2f7ab8860317a18a059f9cd756.r2.cloudflarestorage.com';

const app = express();
app.use(cors());
app.use(express.json());

// Initialize PostgreSQL Connection Pool
const pool = new Pool({
  connectionString: DATABASE_URL,
  ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false
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
// REST API Endpoints
// --------------------------------------------------------------------

// 1. Health & Status
app.get('/api/v1/health', async (req, res) => {
  try {
    const dbRes = await pool.query('SELECT NOW()');
    res.json({ status: 'OK', timestamp: dbRes.rows[0].now, service: 'NØTUNE Social Cloud' });
  } catch (err) {
    res.status(500).json({ status: 'ERROR', message: err.message });
  }
});

// 2. User Auth & Session
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

// 3. Friend Network & Requests
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

app.post('/api/v1/friends/requests', authenticateToken, async (req, res) => {
  const { targetUserId } = req.body;
  if (!targetUserId) return res.status(400).json({ error: 'targetUserId required' });

  try {
    const query = `
      INSERT INTO friendships (requester_id, addressee_id, status)
      VALUES ($1, $2, 'PENDING')
      ON CONFLICT (requester_id, addressee_id) DO NOTHING
      RETURNING id, status, created_at;
    `;
    const result = await pool.query(query, [req.user.id, targetUserId]);
    res.json({ request: result.rows[0] || { status: 'EXISTS' } });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 4. Social Feed & Posts
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

// 5. Post Reactions & Comments
app.post('/api/v1/posts/:id/react', authenticateToken, async (req, res) => {
  const postId = req.params.id;
  const { reactionType } = req.body; // LOVE, FIRE, FEELS, etc.

  try {
    const upsertQuery = `
      INSERT INTO post_reactions (post_id, user_id, reaction_type)
      VALUES ($1, $2, $3)
      ON CONFLICT (post_id, user_id) DO UPDATE SET reaction_type = EXCLUDED.reaction_type
      RETURNING *;
    `;
    await pool.query(upsertQuery, [postId, req.user.id, reactionType]);
    res.json({ success: true, reaction: reactionType });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// 6. Direct Messages & Song Suggestions
app.get('/api/v1/messages/:friendId', authenticateToken, async (req, res) => {
  const friendId = req.params.friendId;
  try {
    const query = `
      SELECT * FROM direct_messages
      WHERE (sender_id = $1 AND receiver_id = $2) OR (sender_id = $2 AND receiver_id = $1)
      ORDER BY created_at ASC
      LIMIT 100;
    `;
    const result = await pool.query(query, [req.user.id, friendId]);
    res.json({ messages: result.rows });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/v1/messages', authenticateToken, async (req, res) => {
  const { receiverId, messageType, textContent, songId, songTitle, artistName } = req.body;
  try {
    const query = `
      INSERT INTO direct_messages (sender_id, receiver_id, message_type, text_content, song_id, song_title, artist_name)
      VALUES ($1, $2, $3, $4, $5, $6, $7)
      RETURNING *;
    `;
    const result = await pool.query(query, [
      req.user.id,
      receiverId,
      messageType || 'TEXT',
      textContent || null,
      songId || null,
      songTitle || null,
      artistName || null
    ]);

    const msg = result.rows[0];
    broadcastWebSocketMessage(receiverId, { type: 'NEW_MESSAGE', message: msg });
    res.json({ message: msg });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// --------------------------------------------------------------------
// WebSocket Server for Real-Time Presence & Messaging
// --------------------------------------------------------------------

const server = http.createServer(app);
const wss = new WebSocket.Server({ server });
const connectedClients = new Map(); // userId -> WebSocket connection

wss.on('connection', (ws, req) => {
  let authenticatedUserId = null;

  ws.on('message', async (data) => {
    try {
      const payload = JSON.parse(data.toString());
      if (payload.type === 'AUTHENTICATE') {
        authenticatedUserId = payload.userId;
        connectedClients.set(authenticatedUserId, ws);
        await updatePresence(authenticatedUserId, 'ONLINE');
        ws.send(JSON.stringify({ type: 'AUTHENTICATED', status: 'SUCCESS' }));
      } else if (payload.type === 'HEARTBEAT_PRESENCE') {
        if (authenticatedUserId) {
          await updatePresence(authenticatedUserId, payload.status || 'ONLINE', payload.currentTrack);
        }
      }
    } catch (e) {
      console.error('WebSocket payload error:', e.message);
    }
  });

  ws.on('close', async () => {
    if (authenticatedUserId) {
      connectedClients.delete(authenticatedUserId);
      await updatePresence(authenticatedUserId, 'OFFLINE');
    }
  });
});

async function updatePresence(userId, status, trackData = {}) {
  try {
    const query = `
      INSERT INTO user_presence (user_id, status, current_track_id, current_track_title, current_artist_name, last_active_at)
      VALUES ($1, $2, $3, $4, $5, NOW())
      ON CONFLICT (user_id) DO UPDATE SET
        status = EXCLUDED.status,
        current_track_id = EXCLUDED.current_track_id,
        current_track_title = EXCLUDED.current_track_title,
        current_artist_name = EXCLUDED.current_artist_name,
        last_active_at = NOW();
    `;
    await pool.query(query, [
      userId,
      status,
      trackData.trackId || null,
      trackData.title || null,
      trackData.artist || null
    ]);
  } catch (e) {
    console.error('Failed to update presence:', e.message);
  }
}

function broadcastWebSocketMessage(targetUserId, payload) {
  const targetWs = connectedClients.get(targetUserId);
  if (targetWs && targetWs.readyState === WebSocket.OPEN) {
    targetWs.send(JSON.stringify(payload));
  }
}

// Start Server
server.listen(PORT, '0.0.0.0', () => {
  console.log(`🚀 NØTUNE Cloud Server active on port ${PORT}`);
});
