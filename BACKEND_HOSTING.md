# NØTUNE Listen Together - 24/7 Free Cloud Hosting & 100k Concurrency Architecture

This guide explains how to host the NØTUNE backend server and database for **100% FREE** on cloud platforms without hosting on your personal local machine.

---

## ⚡ How NØTUNE Handles 100,000 Concurrent Users (100k CCU)

To serve **100,000 concurrent users** without crashing or incurring huge bandwidth bills:
1. **Audio Streams Direct from CDN**: Audio files are streamed directly from YouTube CDNs / Local MediaStore to user devices. The backend server **never proxies audio bytes**—it only handles lightweight JSON events (~1 KB/s per user).
2. **WebSocket Memory Optimization**: 100k open WebSockets require ~2.5 GB to 4 GB RAM. Platforms like **Oracle Cloud Always Free (24 GB RAM)** or **Cloudflare Worker Hibernation** easily support this volume.

---

## 🏆 Top 100% Free 24/7 Cloud Platforms

### 1. Oracle Cloud Infrastructure (OCI) — "Always Free" VM (RECOMMENDED FOR 100K CCU)
* **Specs**: 4 ARM vCPUs (Ampere A1), **24 GB RAM**, 200 GB NVMe Storage, **10 TB/month Free Bandwidth**.
* **Cost**: **$0 / Forever** (24/7 active, never sleeps).
* **Capacity**: **100,000+ simultaneous WebSocket connections** effortlessly fit into 24 GB RAM.

#### Quick Oracle Cloud Setup:
1. Create a free account on [Oracle Cloud Infrastructure](https://www.oracle.com/cloud/free/).
2. Launch an **Ampere ARM Instance** (Select 4 OCPUs, 24 GB RAM, Ubuntu 24.04 LTS).
3. Connect via SSH and run:
   ```bash
   git clone https://github.com/MrDSP18/notune.git
   cd notune/notune-backend
   sudo docker-compose up -d --build
   ```
4. Your server will be live 24/7 at `wss://your-oracle-ip:10000/ws` with local PostgreSQL running inside Docker!

---

### 2. Cloudflare Workers + D1 Database + R2 Storage (GLOBAL SERVERLESS EDGE)
* **Specs**: Runs across 300+ edge locations worldwide with automatic DDoS protection.
* **WebSocket Hibernation**: Idle WebSockets consume 0 RAM billing on Cloudflare Workers.
* **Free Tier**: 100,000 HTTP requests/day, 5 Million D1 SQL reads/day, 10 GB R2 media storage with **$0 egress fees**.
* **Cost**: **$0 / Forever**.

#### Quick Cloudflare Deployment:
1. Install Wrangler CLI: `npm install -g wrangler`
2. Login to Cloudflare: `wrangler login`
3. Deploy NØTUNE Edge Worker:
   ```bash
   cd notune-backend
   wrangler deploy
   ```
4. Your server is instantly live worldwide at `wss://notune.<your-subdomain>.workers.dev/ws`!

---

### 3. Supabase (Managed Free PostgreSQL Database)
* **Specs**: 500 MB PostgreSQL Database, 50,000 Monthly Active Users, Realtime WebSocket Broadcast.
* **Cost**: **$0 / Forever**.
* **Setup**: Connect `DATABASE_URL` in `render.yaml` or `notune-backend/.env` to your Supabase PostgreSQL connection string.

---

### 4. Koyeb / Render / Railway (Fast Container Hosting)
* **Render**: Free 512 MB Web Service ([`render.yaml`](file:///home/dharan-25486/Documents/music/V2/notune/render.yaml))
* **Koyeb**: 512 MB RAM instance, 24/7 online, fast Docker deployment.

---

## 🔗 Connecting the App
Once your server is live on Oracle Cloud or Cloudflare Workers, users connect by setting the **Listen Together Server URL** in **Settings > Integrations > Listen Together** or by updating `ListenTogetherServers.kt`.

---
**NØTUNE — Connected Listening at Global Scale.**

