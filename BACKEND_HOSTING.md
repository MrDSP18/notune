# NØTUNE Listen Together - Server Hosting Guide

This guide explains how to host the NØTUNE Listen Together backend server.

## 🚀 One-Click Deployment
The fastest way to host your own server is using **Render** or **Railway**.

### Render (Recommended)
1. Fork this repository.
2. Create a new "Web Service" on [Render](https://render.com/).
3. Connect your fork.
4. Render will automatically detect `render.yaml` and configure the environment.
5. Set the `DATABASE_URL` if you want persistent rooms (optional, default uses in-memory/SQLite).

## 🐳 Docker Deployment
We provide a `Dockerfile` and `docker-compose.yml` for easy hosting.

```bash
cd notune-backend
docker-compose up -d
```

## 🛠 Manual Installation
Requires Node.js 18+.

```bash
cd notune-backend
npm install
npm start
```

## 🔗 Connecting the App
Once your server is live, users can connect to it by changing the "Listen Together Server URL" in **Settings > Integrations > Listen Together**.

---
**NØTUNE — Connected Listening.**
