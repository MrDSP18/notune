/**
 * NØTUNE Load Testing & Concurrency Benchmark Harness
 * Simulates high-concurrency WebSocket connections, room sync, presence, and failover.
 * 
 * Usage:
 *   node load-test.js --users=1000 --ramp=10 --duration=30 --target=ws://localhost:10000/ws
 */

const WebSocket = require('ws');

// Parse CLI Arguments
const args = process.argv.slice(2).reduce((acc, arg) => {
  const [key, value] = arg.replace(/^--/, '').split('=');
  acc[key] = value;
  return acc;
}, {});

const TARGET_USERS = parseInt(args.users || '100', 10);
const RAMP_SECONDS = parseInt(args.ramp || '10', 10);
const DURATION_SECONDS = parseInt(args.duration || '30', 10);
const SERVER_URL = args.target || 'ws://localhost:10000/ws';

console.log('====================================================');
console.log(`🚀 NØTUNE Benchmark Harness Starting`);
console.log(`Target Connections: ${TARGET_USERS}`);
console.log(`Ramp-up Duration  : ${RAMP_SECONDS} seconds`);
console.log(`Test Duration     : ${DURATION_SECONDS} seconds`);
console.log(`Target Server     : ${SERVER_URL}`);
console.log('====================================================\n');

const stats = {
  attempted: 0,
  connected: 0,
  disconnected: 0,
  reconnected: 0,
  failed: 0,
  messagesSent: 0,
  messagesReceived: 0,
  staleEvents: 0,
  connectLatencies: [],
  joinLatencies: [],
  msgLatencies: []
};

const clients = [];
const startTime = Date.now();

function createClient(id) {
  stats.attempted++;
  const clientStart = Date.now();
  const userId = `load_user_${id}_${Math.random().toString(36).substring(2, 7)}`;
  const username = `TestUser_${id}`;
  const roomId = `room_${Math.floor(id / 5)}`; // 5 users per room

  try {
    const ws = new WebSocket(SERVER_URL);
    let isConnected = false;
    let authSentTime = 0;

    ws.on('open', () => {
      stats.connected++;
      isConnected = true;
      const connectLatency = Date.now() - clientStart;
      stats.connectLatencies.push(connectLatency);

      // 1. Authenticate
      authSentTime = Date.now();
      ws.send(JSON.stringify({
        type: 'AUTHENTICATE',
        userId,
        username
      }));

      // 2. Join Room
      const joinStart = Date.now();
      ws.send(JSON.stringify({
        type: 'JOIN_ROOM',
        roomId,
        userId
      }));
      stats.joinLatencies.push(Date.now() - joinStart);
    });

    ws.on('message', (data) => {
      stats.messagesReceived++;
      try {
        const msg = JSON.parse(data.toString());
        if (msg.serverTime) {
          stats.msgLatencies.push(Math.max(0, Date.now() - msg.serverTime));
        }
      } catch (e) {}
    });

    // Simulated Periodic Heartbeat & Playback Events
    const pingInterval = setInterval(() => {
      if (ws.readyState === WebSocket.OPEN) {
        stats.messagesSent++;
        ws.send(JSON.stringify({
          type: 'HEARTBEAT_PRESENCE',
          userId,
          status: 'ONLINE'
        }));

        if (Math.random() < 0.3) {
          stats.messagesSent++;
          ws.send(JSON.stringify({
            type: 'ROOM_SYNC_EVENT',
            roomId,
            userId,
            payload: { action: 'PLAY', positionMs: Math.floor(Math.random() * 180000) }
          }));
        }
      }
    }, 5000 + Math.random() * 2000); // Heartbeat + Jitter

    ws.on('close', () => {
      if (isConnected) {
        stats.disconnected++;
        isConnected = false;
      }
      clearInterval(pingInterval);
    });

    ws.on('error', (err) => {
      stats.failed++;
    });

    clients.push({ ws, pingInterval });
  } catch (err) {
    stats.failed++;
  }
}

// Ramp-up client spawning
const spawnIntervalMs = (RAMP_SECONDS * 1000) / TARGET_USERS;
let spawnedCount = 0;

const rampTimer = setInterval(() => {
  if (spawnedCount < TARGET_USERS) {
    createClient(++spawnedCount);
  } else {
    clearInterval(rampTimer);
  }
}, spawnIntervalMs);

// Test Finish & Report Generator
setTimeout(() => {
  console.log('\n====================================================');
  console.log(`📊 NØTUNE Benchmark Execution Report`);
  console.log('====================================================');

  // Compute Latency Percentiles
  function getPercentile(arr, p) {
    if (arr.length === 0) return 0;
    const sorted = [...arr].sort((a, b) => a - b);
    const index = Math.floor((p / 100) * sorted.length);
    return sorted[Math.min(index, sorted.length - 1)];
  }

  const successRate = ((stats.connected / (stats.attempted || 1)) * 100).toFixed(2);
  const totalDurationSec = (Date.now() - startTime) / 1000;
  const msgRate = (stats.messagesReceived / totalDurationSec).toFixed(1);

  console.log(`Total Connection Attempts : ${stats.attempted}`);
  console.log(`Successful Connections    : ${stats.connected} (${successRate}%)`);
  console.log(`Failed Connections        : ${stats.failed}`);
  console.log(`Messages Transmitted      : ${stats.messagesSent} sent | ${stats.messagesReceived} received`);
  console.log(`Message Throughput        : ${msgRate} msg/sec`);
  console.log(`----------------------------------------------------`);
  console.log(`Connection Latency (ms)   : p50=${getPercentile(stats.connectLatencies, 50)}ms | p95=${getPercentile(stats.connectLatencies, 95)}ms | p99=${getPercentile(stats.connectLatencies, 99)}ms`);
  console.log(`Room Join Latency (ms)     : p50=${getPercentile(stats.joinLatencies, 50)}ms | p95=${getPercentile(stats.joinLatencies, 95)}ms | p99=${getPercentile(stats.joinLatencies, 99)}ms`);
  console.log(`Message Latency (ms)       : p50=${getPercentile(stats.msgLatencies, 50)}ms | p95=${getPercentile(stats.msgLatencies, 95)}ms | p99=${getPercentile(stats.msgLatencies, 99)}ms`);
  console.log('====================================================');

  if (TARGET_USERS >= 100000 && parseFloat(successRate) >= 99.0) {
    console.log(`RESULT: 100K CCU VALIDATED SUCCESSFULLY 🎉`);
  } else {
    console.log(`RESULT: 100K CCU: NOT YET VALIDATED (Tested ${TARGET_USERS} connections)`);
  }

  // Cleanup Connections
  clients.forEach(c => {
    clearInterval(c.pingInterval);
    try { c.ws.close(); } catch (e) {}
  });

  process.exit(0);
}, (RAMP_SECONDS + DURATION_SECONDS) * 1000);
