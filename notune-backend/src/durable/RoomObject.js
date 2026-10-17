// NØTUNE Realtime Room Durable Object
// Uses Cloudflare WebSocket Hibernation API for zero-RAM idle scaling

export class RoomObject {
  constructor(state, env) {
    this.state = state;
    this.env = env;
    this.roomState = {
      roomId: "",
      sequence: 0,
      trackId: "",
      isPlaying: false,
      positionMs: 0,
      startedAt: Date.now(),
      hostId: "",
      members: []
    };

    // Load persisted state if available
    this.state.blockConcurrencyWhile(async () => {
      const stored = await this.state.storage.get("roomState");
      if (stored) {
        this.roomState = stored;
      }
    });
  }

  async fetch(request) {
    if (request.headers.get("Upgrade") === "websocket") {
      const pair = new WebSocketPair();
      const [client, server] = Object.values(pair);

      // Use Cloudflare WebSocket Hibernation API
      this.state.acceptWebSocket(server);

      // Send current initial state sync
      server.send(JSON.stringify({
        type: "SYNC_STATE",
        sequence: this.roomState.sequence,
        state: this.roomState
      }));

      return new Response(null, { status: 101, webSocket: client });
    }

    // HTTP GET room state
    return new Response(JSON.stringify({
      status: "ok",
      activeConnections: this.state.getWebSockets().length,
      roomState: this.roomState
    }), {
      headers: { "Content-Type": "application/json" }
    });
  }

  async webSocketMessage(ws, message) {
    try {
      const data = JSON.parse(message);
      this.roomState.sequence += 1;

      switch (data.type) {
        case "PLAYBACK_STARTED":
          this.roomState.isPlaying = true;
          this.roomState.positionMs = data.positionMs || 0;
          break;
        case "SEEKED":
          this.roomState.positionMs = data.positionMs || 0;
          break;
        case "TRACK_CHANGED":
          this.roomState.trackId = data.trackId || "";
          this.roomState.positionMs = 0;
          break;
        case "USER_REACTED":
          // Broadcast reaction without mutating core playback state
          break;
      }

      // Persist state asynchronously
      await this.state.storage.put("roomState", this.roomState);

      // Broadcast payload to all hibernating/connected WebSocket clients
      const broadcastPayload = JSON.stringify({
        type: data.type,
        sequence: this.roomState.sequence,
        actor: data.actor || "user",
        payload: data,
        state: this.roomState
      });

      for (const clientWs of this.state.getWebSockets()) {
        try {
          clientWs.send(broadcastPayload);
        } catch (e) {
          // Hibernation handles closed sockets automatically
        }
      }
    } catch (err) {
      console.error("RoomObject Hibernation message error:", err);
    }
  }

  async webSocketClose(ws, code, reason, wasClean) {
    // Cloudflare Hibernation automatically manages closed WebSockets in getWebSockets()
  }

  async webSocketError(ws, error) {
    console.error("RoomObject WebSocket Error:", error);
  }
}
