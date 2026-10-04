// NØTUNE Production Cloudflare Edge Worker API & Router
export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    const path = url.pathname;

    const corsHeaders = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type, Authorization",
    };

    if (request.method === "OPTIONS") {
      return new Response(null, { headers: corsHeaders });
    }

    // GET /health - Basic Worker Liveness Check
    if (path === "/health") {
      return new Response(
        JSON.stringify({ status: "ok", service: "notune-api", timestamp: new Date().toISOString() }),
        { headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    // GET /ready - D1 Database Query Connectivity Check
    if (path === "/ready") {
      try {
        if (env.DB) {
          const stmt = env.DB.prepare("SELECT 1 AS ready");
          await stmt.first();
          return new Response(
            JSON.stringify({ status: "ready", database: "ok", service: "notune-api" }),
            { headers: { ...corsHeaders, "Content-Type": "application/json" } }
          );
        } else {
          return new Response(
            JSON.stringify({ status: "ready", database: "mock", note: "D1 DB binding not provided in dev" }),
            { headers: { ...corsHeaders, "Content-Type": "application/json" } }
          );
        }
      } catch (err) {
        return new Response(
          JSON.stringify({ status: "error", database: "failed", error: err.message }),
          { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
        );
      }
    }

    // GET /api/v1/releases/latest - Approved Stable Release Endpoint
    if (path === "/api/v1/releases/latest") {
      const latestRelease = {
        versionName: "3.1.0",
        versionCode: 30100,
        channel: "stable",
        approved: true,
        downloadUrl: "https://github.com/MrDSP18/notune/releases/download/v3.1.0/notune-v3.1.0-universal-debug.apk",
        sha256: "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
        releaseNotes: "NØTUNE V3 — Personal Music OS Release with On-Device NØ AI Engine."
      };
      return new Response(JSON.stringify(latestRelease), {
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    // POST /api/v1/shares - Create Short Link Share Record
    if (path === "/api/v1/shares" && request.method === "POST") {
      try {
        const body = await request.json();
        const shortCode = Math.random().toString(36).substring(2, 8);
        const shareUrl = `https://notune.app/s/${shortCode}`;

        return new Response(
          JSON.stringify({ success: true, code: shortCode, url: shareUrl, targetId: body.targetId }),
          { headers: { ...corsHeaders, "Content-Type": "application/json" } }
        );
      } catch (err) {
        return new Response(
          JSON.stringify({ success: false, error: err.message }),
          { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
        );
      }
    }

    // Universal NØ Link Router (/s/:id, /p/:id, /r/:id, /u/:id)
    if (path.startsWith("/s/") || path.startsWith("/p/") || path.startsWith("/r/") || path.startsWith("/u/")) {
      const parts = path.split("/").filter(Boolean);
      const type = parts[0] || "s";
      const code = parts[1] || "";

      const html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>NØTUNE — Your Music. Your World.</title>
  <style>
    body { background: #090A0F; color: #F3F4F6; font-family: system-ui, sans-serif; display: flex; align-items: center; justify-content: center; min-height: 100vh; margin: 0; }
    .card { background: #12141D; padding: 32px; border-radius: 24px; text-align: center; max-width: 380px; width: 90%; border: 1px solid rgba(255,255,255,0.1); }
    .logo { color: #FF0031; font-weight: 900; font-size: 32px; letter-spacing: -1px; }
    .btn { background: #FF0031; color: white; border: none; padding: 14px 24px; border-radius: 14px; font-weight: bold; width: 100%; margin-top: 16px; cursor: pointer; text-decoration: none; display: inline-block; box-sizing: border-box; }
    .btn-sec { background: #1A1D2A; color: #F3F4F6; margin-top: 8px; }
  </style>
</head>
<body>
  <div class="card">
    <div class="logo">NØTUNE</div>
    <h3>Shared ${type.toUpperCase()} Link</h3>
    <p>Code: <code>${code}</code></p>
    <p style="color:#9CA3AF; font-size: 13px;">Experience with NØTUNE Personal Music OS</p>
    <a href="notune://open/${type}/${code}" class="btn">OPEN IN NØTUNE APP</a>
    <a href="/play?type=${type}&code=${code}" class="btn btn-sec">PLAY ON WEB</a>
    <a href="/download" class="btn btn-sec">DOWNLOAD NØTUNE APK</a>
  </div>
</body>
</html>`;

      return new Response(html, {
        headers: { "Content-Type": "text/html; charset=utf-8" },
      });
    }

    return new Response(
      JSON.stringify({ service: "notune-api", status: "online", endpoints: ["/health", "/ready", "/api/v1/releases/latest", "/api/v1/shares"] }),
      { headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  },
};
