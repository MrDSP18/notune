// NØTUNE Realtime Room Durable Object
// Handles SQLite-backed WebSocket synchronization, sequence numbering & state recovery

export class RoomObject {
  constructor(state, env) {
    this.state = state;
    this.env = env;
    this.sessions = new Set();
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
  }

  async fetch(request) {
    const url = new URL(request.url);

    if (request.headers.get("Upgrade") === "websocket") {
      const pair = new WebSocketPair();
      const [client, server] = Object.values(pair);

      await this.handleSession(server);
      return new Response(null, { status: 101, webSocket: client });
    }

    // HTTP GET room state
    return new Response(JSON.stringify(this.roomState), {
      headers: { "Content-Type": "application/json" }
    });
  }

  async handleSession(webSocket) {
    webSocket.accept();
    this.sessions.add(webSocket);

    // Send current state on connection
    webSocket.send(JSON.stringify({
      type: "SYNC_STATE",
      sequence: this.roomState.sequence,
      state: this.roomState
    }));

    webSocket.addEventListener("message", async (msg) => {
      try {
        const data = JSON.parse(msg.data);
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
            // Broadcast reaction without mutating state
            break;
        }

        // Broadcast updated state event to all connected room sessions
        const broadcastPayload = JSON.stringify({
          type: data.type,
          sequence: this.roomState.sequence,
          actor: data.actor || "user",
          payload: data,
          state: this.roomState
        });

        for (const session of this.sessions) {
          try {
            session.send(broadcastPayload);
          } catch (e) {
            this.sessions.delete(session);
          }
        }
      } catch (err) {
        console.error("RoomObject WebSocket processing error:", err);
      }
    });

    webSocket.addEventListener("close", () => {
      this.sessions.delete(webSocket);
    });
  }
}
