# NØTUNE Cloud Backend Architecture & Free Database Deployment Guide

This repository contains the backend service and database schema definitions for the **NØTUNE** ecosystem.

## 1. Dual-Database Architecture Overview

```text
                    NØTUNE ANDROID APP
                           │
             ┌─────────────┴─────────────┐
             │                           │
       LOCAL DEVICE                 CLOUD BACKEND
             │                           │
        Room / SQLite              PostgreSQL
             │                           │
    ┌────────┼────────┐            ┌─────┼─────┐
    │        │        │            │     │     │
  Songs   History   DNA          Users Rooms  Sync
  Queue   Playlists AI data      Auth  Social Profile
  Cache   Settings  Downloads
```

- **Local Phone Database (Room / SQLite)**: On-device database for local songs, playback queue, listening history, favorites, playlists, Music DNA, offline intelligence, customization settings, and lyrics cache. 100% functional offline without internet.
- **Cloud Backend Database (PostgreSQL)**: Handles account identity, OAuth token refresh, multi-device profile sync, Rooms, and social features.

---

## 2. Deploying Free PostgreSQL Cloud Database

You can deploy the NØTUNE PostgreSQL database for **100% free** on any of these platforms:

### Option A: Supabase (Recommended Free Tier)
1. Go to [supabase.com](https://supabase.com) and create a free account.
2. Click **New Project** and name it `notune-cloud`.
3. Set your Database Password.
4. Go to **SQL Editor** in the Supabase Dashboard.
5. Paste the contents of [`schema.sql`](./schema.sql) and click **Run**.
6. Copy your database connection string from **Project Settings → Database → Connection String (URI)**.

### Option B: Neon.tech (Serverless Free Tier)
1. Go to [neon.tech](https://neon.tech) and create a free account.
2. Create a project named `notune-db`.
3. Open the **SQL Editor**, paste [`schema.sql`](./schema.sql), and run it.
4. Copy the connection string (e.g. `postgresql://alex:pass@ep-xyz.region.aws.neon.tech/neondb`).

### Option C: Render.com (Free Tier)
1. Go to [render.com](https://render.com) and create a New **PostgreSQL** instance.
2. Connect to the database using `psql` or DBeaver and run `schema.sql`.

---

## 3. Local Development Database Setup

To run a PostgreSQL database locally with Docker:

```bash
cd notune-backend
docker-compose up -d
```

This starts PostgreSQL on `localhost:5432` and initializes `schema.sql` automatically.

---

## 4. Authentication Architecture & Token Security

NØTUNE backend services enforce cryptographic authentication using HS256 JSON Web Tokens with strict fail-closed security properties:

### A. Secret Configuration & Fail-Closed Startup
- `JWT_SECRET` is **mandatory** and must be at least 32 characters (256 bits).
- All predictable and hardcoded defaults (`notune_production_jwt_secret_key_2026`, `secret`, `changeme`, etc.) are actively detected and rejected.
- In production (`NODE_ENV=production` or `STRICT_AUTH=true`), the service terminates immediately on startup (`process.exit(1)`) if `JWT_SECRET` is missing, short, or placeholder.
- Generate a production secret using OpenSSL:
  ```bash
  openssl rand -hex 32
  ```

### B. Route Access Classification
- **Public**: Health probes (`/health`, `/ready`), metadata resolution (`/api/v1/metadata/*`), registration (`POST /api/v1/auth/register`), and login (`POST /api/v1/auth/login`).
- **Protected (`requireAuth`)**: Social connections (`GET /api/v1/friends`), post creation (`POST /api/v1/posts`), identity self-inspection (`GET /api/v1/auth/me`), and cloud sync (`POST /api/v1/sync`). Unauthenticated requests are rejected with `401 Unauthorized` and **never** fall back to a shared guest identity.
- **Optional (`optionalAuth`)**: Public feed (`GET /api/v1/feed`). If no token is provided, public content is returned without user context. If an invalid or expired token is provided, the request is rejected with `401`.

### C. WebSocket Authentication
- WebSocket connections cannot join rooms or broadcast events without explicit authentication.
- Clients must send `AUTHENTICATE` with a valid JWT token signed by `JWT_SECRET`.
- The user identity is strictly extracted from verified claims (`id` UUID); client-supplied `userId` fields are discarded.
- Forged, expired, or missing tokens terminate the WebSocket connection with error code `4001` or `4003`.

---

## 5. Cloudflare D1 Sync Ingestion

The Worker sync endpoint accepts authenticated event batches at `POST /api/v1/sync`.
It verifies the HS256 bearer token against the `JWT_SECRET` Worker secret, scopes
all records to the verified token subject (`id` claim), and persists events in D1.
The client must retain any event that is not explicitly returned in
`acknowledgedEventIds`.

The D1 migrations live in `d1-migrations/` deliberately: the existing
`migrations/` directory contains PostgreSQL migrations and must not be applied to D1.

Before deploying:

1. Set the Cloudflare Worker secret to the exact same signing secret used by the
   authentication API that issues the app's JWTs. Do not commit or paste the value
   into source control:
   `npx wrangler secret put JWT_SECRET`
2. Apply the D1 migration:
   `npx wrangler d1 migrations apply notune-db --remote`
3. Deploy from this directory:
   `npx wrangler deploy`
4. Verify `GET /ready` reports the D1 database as ready, then test an authenticated
   sync from a signed-in app account.

The endpoint intentionally returns 503 when D1 or the JWT secret is unavailable,
401 for missing/invalid tokens, and never trusts a user ID supplied in the JSON body.
The auth API and Worker must share the same JWT signing secret; rotating it requires
updating both services. No deployment or secret provisioning is performed by CI.
