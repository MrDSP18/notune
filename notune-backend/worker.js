/**
 * NØTUNE Cloudflare Worker Serverless Backend Service
 * Serves global API requests, Cloudflare D1 SQL database & R2 Storage.
 */

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);
    const path = url.pathname;

    // CORS Headers for global mobile & web requests
    const corsHeaders = {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization',
    };

    if (request.method === 'OPTIONS') {
      return new Response(null, { headers: corsHeaders });
    }

    try {
      // 0. Root Welcome Route
      if (path === '/' || path === '') {
        return Response.json(
          { status: 'ACTIVE', name: 'NØTUNE Social Cloud Ecosystem', health: '/api/v1/health' },
          { headers: corsHeaders }
        );
      }

      // 1. Health Check
      if (path === '/api/v1/health') {
        return Response.json(
          { status: 'OK', service: 'NØTUNE Cloudflare Worker Edge', accountId: 'c4062f2f7ab8860317a18a059f9cd756' },
          { headers: corsHeaders }
        );
      }

      // 2. User Authentication
      if (path === '/api/v1/auth/register' && request.method === 'POST') {
        const body = await request.json();
        const displayName = body.displayName || 'Anonymous Listener';
        const userId = crypto.randomUUID();

        if (env.DB) {
          await env.DB.prepare(
            `INSERT INTO users (id, display_name, email) VALUES (?, ?, ?)`
          ).bind(userId, displayName, body.email || null).run();
        }

        return Response.json({
          status: 'SUCCESS',
          token: `token_${userId}`,
          user: { id: userId, displayName }
        }, { headers: corsHeaders });
      }

      // 3. Social Feed
      if (path === '/api/v1/feed' && request.method === 'GET') {
        let posts = [];
        if (env.DB) {
          const { results } = await env.DB.prepare(
            `SELECT * FROM social_posts ORDER BY created_at DESC LIMIT 50`
          ).all();
          posts = results;
        } else {
          posts = [
            {
              id: 'post_cf_1',
              author_name: 'Aarav Sharma',
              post_type: 'SONG',
              song_title: 'Until I Found You',
              artist_name: 'Stephen Sanchez',
              caption: 'Cloudflare Edge Social Feed Live! ⚡',
              likes_count: 88,
              created_at: new Date().toISOString()
            }
          ];
        }
        return Response.json({ posts }, { headers: corsHeaders });
      }

      // 4. Send Message / Song Suggestion
      if (path === '/api/v1/messages' && request.method === 'POST') {
        const body = await request.json();
        const msgId = crypto.randomUUID();
        return Response.json({
          status: 'SENT',
          message: { id: msgId, ...body, created_at: new Date().toISOString() }
        }, { headers: corsHeaders });
      }

      // Default Response
      return Response.json({ error: 'Endpoint Not Found', path }, { status: 404, headers: corsHeaders });
    } catch (err) {
      return Response.json({ error: err.message }, { status: 500, headers: corsHeaders });
    }
  }
};
