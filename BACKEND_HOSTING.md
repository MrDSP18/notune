# NØTUNE Backend Infrastructure & High-Scale Architecture

This document outlines the architecture, security hardening, cloud deployment strategies, and scalability targets for the NØTUNE Listen Together and Cloud Ecosystem.

> [!IMPORTANT]
> **Scalability Target**: Designed to scale toward 100,000 concurrent connections, subject to load testing, cloud-provider quotas, networking limits, database capacity, and workload characteristics.
> 
> *Do NOT treat 100,000 CCU as an absolute guarantee or zero-cost promise. Capacity MUST be verified through representative load testing.*

---

## 🏗 System Architecture & Separation of Concerns

NØTUNE enforces a strict **separation of concerns** between high-bandwidth media streaming and low-bandwidth state synchronization:

```text
YouTube CDN ────────────────────────► NØTUNE Client (Direct Stream)
Local MediaStore ───────────────────► NØTUNE Client (Direct Stream)

NØTUNE Backend ────────┬────────────► Metadata
                       ├────────────► Authentication / JWT
                       ├────────────► Room Sync & Playback Events
                       ├────────────► Dynamic Playlists & Queues
                       ├────────────► Social Feed & Reactions
                       └────────────► Presence & Taste DNA

NØTUNE Backend ────────❌───────────► NO Audio Bytes (Zero Proxying)
```

---

## 🌐 Authoritative Infrastructure Topology

NØTUNE's core infrastructure relies on authoritative primary servers owned and operated within the NØTUNE ecosystem. Third-party fallback servers are maintained strictly as emergency redundancies.

```text
                             NØTUNE Mobile Client
                                      │
                         ┌────────────┴────────────┐
                         │   Cloudflare Edge WAF   │
                         │    Routing & CDN        │
                         └────────────┬────────────┘
                                      │
               ┌──────────────────────┴──────────────────────┐
               │                                             │
   NØTUNE Primary Worker Edge                      NØTUNE Primary API Server
(wss://notune...workers.dev/ws)                (wss://notune-backend...onrender.com/ws)
               │                                             │
               └──────────────────────┬──────────────────────┘
                                      │
                             ┌────────┴────────┐
                             │ PostgreSQL / D1 │
                             │ Data Store      │
                             └─────────────────┘
                                      │
                        [Emergency Fallback Servers]
```

---

## 🚦 Health & Readiness Probes

The backend exposes lightweight health endpoints:

* **`/health` (or `/api/v1/health`)**: Liveness probe. Returns HTTP 200 `{ status: "UP" }` instantly without executing database queries.
* **`/ready`**: Readiness probe. Executes a lightweight `SELECT 1` query to verify PostgreSQL connection pool health before accepting traffic.

---

## 🔒 Security & Authorization Audit

* **JWT Verification**: Validates session tokens on REST APIs and WebSocket handshakes (`AUTHENTICATE`).
* **Room Member Authorization**: Verifies user presence in `room_members` prior to processing playback sync or queue mutations.
* **Rate Limiting**: `express-rate-limit` enforces IP rate limits on `/api/` routes (1,000 req / 15 min).
* **Security Headers**: `helmet` enforces security headers across HTTP endpoints.
* **Data Privacy**: Passwords, secrets, and tokens are never logged or transmitted over unencrypted connections.

---

## 🧪 Load Testing & Benchmark Suite

NØTUNE includes an automated WebSocket and REST load-testing harness in `notune-backend/load-test.js`.

### Running Load Tests
```bash
cd notune-backend

# Run local benchmark with 100 concurrent clients
npm run load-test -- --users=100 --ramp=5 --duration=20

# Run high-concurrency target benchmark
npm run load-test -- --users=1000 --ramp=10 --duration=30 --target=wss://notune.dharansundarapandiyan24.workers.dev/ws
```

### Key Performance Target Metrics

| Metric | Target Standard |
| :--- | :--- |
| **WebSocket Connection Success** | > 99.9% |
| **Room Sync Event Latency** | < 120ms (Global) |
| **Authentication Latency** | < 200ms |
| **Reconnection Success (After Outage)** | > 99.0% |
| **DB Pool Connections** | < 80% Max Pool |
| **Error Rate under Load** | < 0.01% |

---

## 🏆 Deployment Configurations

### 1. Cloudflare Workers (Serverless Edge)
* Deploy script: `npm run deploy:cloudflare`
* Configuration: [`wrangler.toml`](file:///home/dharan-25486/Documents/music/V2/notune/wrangler.toml) & [`worker.js`](file:///home/dharan-25486/Documents/music/V2/notune/notune-backend/worker.js)

### 2. Docker / Oracle Cloud / Render (Node.js + PostgreSQL)
* Deploy script: `sudo docker-compose up -d --build`
* Configuration: [`Dockerfile`](file:///home/dharan-25486/Documents/music/V2/notune/notune-backend/Dockerfile) & [`render.yaml`](file:///home/dharan-25486/Documents/music/V2/notune/render.yaml)

---
**NØTUNE — Connected Listening at Global Scale.**
