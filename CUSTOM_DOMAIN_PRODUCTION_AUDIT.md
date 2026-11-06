# NØTUNE Phase 6 Custom Domain & Production Web Integration Audit

**Audit Date**: 2026-10-04  
**Release Target**: `v3.1.0` (Tag & Release NOT yet created)  
**Target Domain**: `https://notune.app` & `https://www.notune.app`  
**Web Platform Distribution**: `https://notune-web.pages.dev`  
**Backend API Distribution**: `https://notune-api.dharansundarapandiyan24.workers.dev`  

---

## 1. Domain & Route Verification Audit Matrix

| Audit Item | Target | Empirical Result | Status |
|---|---|---|---|
| **DNS Status (`notune.app`)** | CNAME / A record pointing to Cloudflare Pages | No active DNS A/CNAME record returned by `dig notune.app` | **BLOCKED** |
| **DNS Status (`www.notune.app`)** | CNAME record pointing to Cloudflare Pages | No active DNS CNAME record returned by `dig www.notune.app` | **BLOCKED** |
| **Cloudflare Pages Binding** | Domain attached to `notune-web` Pages project | Requires Cloudflare Dashboard Custom Domain binding | **BLOCKED** |
| **HTTPS Status** | TLS 1.3 certificate for `notune.app` | TLS active on `https://notune-web.pages.dev` | **PASS (on pages.dev)** |
| **Canonical Domain Routing** | `https://notune.app` → `notune-web.pages.dev` | Target configured in `app.js` and meta tags | **PASS (code ready)** |
| **Website Root (`/`)** | Serves NØTUNE Web Platform HTML | Verified `HTTP/2 200` (`text/html`) on `notune-web.pages.dev` | **PASS** |
| **Web Player Route (`/player`)** | Serves Web Player desktop SPA | Verified `HTTP/2 200` (`text/html`) on `notune-web.pages.dev` | **PASS** |
| **Share Routes (`/s/*`, `/p/*`, `/r/*`, `/u/*`)** | Serves rich share cards | Verified `HTTP/2 200` (`text/html`) on `notune-web.pages.dev` | **PASS** |
| **Assetlinks Endpoint** | `/.well-known/assetlinks.json` | Verified `HTTP/2 200` (`application/json`) on `notune-web.pages.dev` | **PASS** |
| **API Connectivity** | Edge Worker API endpoints return JSON | Verified `HTTP/2 200` (`application/json`) on `notune-api...` | **PASS** |
| **CORS** | `Access-Control-Allow-Origin: *` | Verified `access-control-allow-origin: *` header present | **PASS** |
| **WebSocket / Rooms** | `wss://notune-api.../api/v1/rooms/:roomId/ws` | Durable Object WebSocket hibernation engine verified | **PASS** |
| **PWA Readiness** | `manifest.json` & `sw.js` | Service Worker and Web Manifest registered & verified | **PASS** |
| **Security Audit** | Zero leaked tokens/keys/localhost URLs | Bundle audit clean (only public config shipped to browser) | **PASS** |
| **Redirect Behavior** | No infinite loops | Clean SPA fallback without redirect loops | **PASS** |

---

## 2. Final Decision

`PHASE 6: BLOCKED — MANUAL CLOUDFLARE ACTION REQUIRED`

---

## 3. Required Manual Cloudflare Dashboard Action Plan

To transition the `notune.app` Custom Domain from **BLOCKED** to **PASS**, the Cloudflare account owner (`MrDSP18`) must execute the following 2-step setup in the Cloudflare Dashboard:

### Step 1: Attach Custom Domain to Cloudflare Pages Project
1. Log into the [Cloudflare Dashboard](https://dash.cloudflare.com).
2. Navigate to **Workers & Pages** -> **Pages** -> **notune-web**.
3. Select the **Custom Domains** tab and click **Set up a custom domain**.
4. Enter `notune.app` and click **Continue**.
5. Repeat for `www.notune.app` (setting canonical redirect to `notune.app`).

### Step 2: Configure Cloudflare DNS Records
1. In the Cloudflare Dashboard, select the **notune.app** DNS zone.
2. Add the following CNAME records:
   - **Type**: `CNAME` | **Name**: `@` | **Target**: `notune-web.pages.dev` | **Proxy status**: `Proxied (Orange Cloud)`
   - **Type**: `CNAME` | **Name**: `www` | **Target**: `notune-web.pages.dev` | **Proxy status**: `Proxied (Orange Cloud)`

---

## 4. Verification Check After DNS Activation

Once the Cloudflare Dashboard setup is complete, run the following verification command to confirm Phase 6 resolution:

```bash
curl -Is https://notune.app/
curl -Is https://notune.app/player
curl -Is https://notune.app/.well-known/assetlinks.json
```

Expected output: `HTTP/2 200` with `server: cloudflare` and `content-type: text/html` / `application/json`.
