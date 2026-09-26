# NØTUNE Backend Infrastructure & High-Scale Architecture

This document outlines the architecture, cloud hosting strategies, and scalability targets for the NØTUNE Listen Together and Cloud Ecosystem.

> [!NOTE]
> **Scalability Target**: Designed to scale toward 100,000 concurrent connections, subject to load testing, cloud-provider quotas, networking limits, and workload characteristics.

---

## 🏗 System Architecture & Data Flow

NØTUNE enforces a strict **separation of concerns** between high-bandwidth media streaming and low-bandwidth state synchronization:

```text
YouTube CDN ────────────────────────► NØTUNE Mobile Client
Local MediaStore ───────────────────► NØTUNE Mobile Client

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

## ⚡ Concurrency Engineering & Realities

Achieving high concurrency (e.g. 100k connections) depends on more than raw RAM capacity. Production stability requires optimizing:

1. **Kernel & TCP File Descriptors**: High `ulimit -n` connection limits.
2. **WebSocket Runtime**: Epoll / WebSocket Hibernation to minimize idle memory.
3. **Heartbeat & Reconnect Storm Mitigation**: Jittered exponential backoff during server reconnects.
4. **Room Message Fanout**: Efficient broad-casting without CPU locking.
5. **Database Connection Pooling**: PgBouncer / Serverless connection pools to stay within DB limits.

---

## 🧪 Load Testing & Benchmark Strategy

Before certifying production readiness, the backend must be benchmarked under simulated load ramps:

```text
10 CCU  ──►  100 CCU  ──►  1,000 CCU  ──►  10,000 CCU  ──►  50,000 CCU  ──►  100,000 CCU
```

### Key Performance Target Metrics

| Metric | Target Standard |
| :--- | :--- |
| **WebSocket Connection Success** | > 99.9% |
| **Room Sync Event Latency** | < 120ms (Global) |
| **Authentication & Token Latency** | < 200ms |
| **Reconnection Success (After Outage)** | > 99.0% |
| **DB Pool Connections** | < 80% Max Pool |
| **Error Rate under Load** | < 0.01% |

---

## 🏆 Free 24/7 Cloud Hosting Options

### 1. Oracle Cloud Infrastructure (OCI) — Always Free VM
* **Specs**: 4 ARM vCPUs (Ampere A1), 24 GB RAM, 200 GB Storage, 10 TB/month Free Egress.
* **Cost**: **$0 / Forever** (24/7 dedicated compute).
* **Setup**:
  ```bash
  cd notune-backend
  sudo docker-compose up -d --build
  ```

### 2. Cloudflare Workers + D1 + R2 Storage (Serverless Edge)
* **Specs**: Global edge network with WebSocket Hibernation API.
* **Cost**: **$0 / Free Tier** (100,000 HTTP requests/day, 5M D1 reads/day).
* **Setup**:
  ```bash
  cd notune-backend
  npm run deploy:cloudflare
  ```

### 3. Supabase / Neon (Managed PostgreSQL)
* **Specs**: Managed relational database with connection pooling and WebSocket broadcast capability.

---

## 🔗 Connecting the App

Primary endpoints are configured in [`ListenTogetherServers.kt`](file:///home/dharan-25486/Documents/music/V2/notune/app/src/main/kotlin/com/music/echo/listentogether/ListenTogetherServers.kt) with dynamic fallback logic:

1. **Primary Edge**: `NØTUNE Cloudflare Edge`
2. **Primary API**: `NØTUNE Cloud Server`
3. **Emergency Fallback**: `Metrolist Server`

---
**NØTUNE — Connected Listening at Global Scale.**


