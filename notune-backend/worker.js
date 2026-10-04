// NØTUNE Global Cloudflare Edge Worker & Router
export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    const path = url.pathname;

    // CORS Headers
    const corsHeaders = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type, Authorization",
    };

    if (request.method === "OPTIONS") {
      return new Response(null, { headers: corsHeaders });
    }

    // GET /api/v1/releases/latest
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

    // Universal NØ Link Web Router (/s/:id, /p/:id, /r/:id, /u/:id)
    if (path.startsWith("/s/") || path.startsWith("/p/") || path.startsWith("/r/") || path.startsWith("/u/")) {
      valParts = path.split("/").filter(Boolean);
      valType = valParts[0];
      valCode = valParts[1] || "";

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
    <h3>Shared ${valType.toUpperCase()} Link</h3>
    <p>Code: <code>${valCode}</code></p>
    <p style="color:#9CA3AF; font-size: 13px;">Experience with NØTUNE Personal Music OS</p>
    <a href="notune://open/${valType}/${valCode}" class="btn">OPEN IN NØTUNE APP</a>
    <a href="/play?type=${valType}&code=${valCode}" class="btn btn-sec">PLAY ON WEB</a>
    <a href="/download" class="btn btn-sec">DOWNLOAD NØTUNE APK</a>
  </div>
</body>
</html>`;

      return new Response(html, {
        headers: { "Content-Type": "text/html; charset=utf-8" },
      });
    }

    return new Response(JSON.stringify({ message: "NØTUNE Worker API Active" }), {
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });
  },
};
