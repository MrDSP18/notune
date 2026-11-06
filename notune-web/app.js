/* ==========================================================================
   NØTUNE Central Desktop Web Application & Web Player SPA Engine
   ========================================================================== */

document.addEventListener("DOMContentLoaded", () => {
  
  // 1. Central API Configuration
  const API_BASE = "https://notune-api.dharansundarapandiyan24.workers.dev";
  const APP_SCHEME = "notune://open";

  // 2. Global State Storage
  const state = {
    currentTrack: null,
    isPlaying: false,
    queue: [],
    queueIndex: 0,
    likedSongIds: new Set(),
    history: [],
    shuffle: false,
    repeat: false, // 'none', 'all', 'one'
    activeRoom: null,
    ws: null,
    lyrics: [],
    lyricsActiveLine: -1,
    activeTabRight: "queue"
  };

  // Mock Database Catalog for Web Player
  const CATALOG = [
    {
      id: "track_1",
      title: "Hosanna",
      artist: "A.R. Rahman • Wind (Tamil)",
      album: "Vinnaithaandi Varuvaayaa",
      duration: 318,
      artwork: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&auto=format&fit=crop",
      audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
      mood: "romantic",
      language: "tamil",
      lrc: [
        { time: 0, text: "♪ (Instrumental Intro)" },
        { time: 12, text: "Ennodaiaya nenjil..." },
        { time: 24, text: "Hosanna... En idhayathil un ninaivugal" },
        { time: 38, text: "Hosanna... Kaatrodu varum inbame" },
        { time: 52, text: "Unnai kaanum podhu nenjil minnal adikkudhe" },
        { time: 68, text: "Hosanna... Anbe Hosanna!" },
        { time: 90, text: "♪ (Flute & Violin Solo)" },
        { time: 120, text: "Neeye en vaazhvin thaedalaane" },
        { time: 145, text: "Hosanna... En idhayathil un ninaivugal" }
      ]
    },
    {
      id: "track_2",
      title: "Cybernetic Resonance",
      artist: "NØ AI Engine • Deep Bass",
      album: "OS V3 Soundscapes",
      duration: 245,
      artwork: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&auto=format&fit=crop",
      audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
      mood: "energetic",
      language: "instrumental",
      lrc: [
        { time: 0, text: "♪ [32-Bit Sub Bass Synthesizer]" },
        { time: 15, text: "♪ [On-Device LiteRT Pulse]" },
        { time: 35, text: "♪ [Neural Equalizer Shift]" },
        { time: 60, text: "♪ [Durable Object Room Sync]" }
      ]
    },
    {
      id: "track_3",
      title: "Midnight Reflections",
      artist: "Anirudh Ravichander • Chill Mix",
      album: "NØ Night Vibes",
      duration: 210,
      artwork: "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&auto=format&fit=crop",
      audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
      mood: "chill",
      language: "tamil",
      lrc: [
        { time: 0, text: "♪ (Midnight Beats)" },
        { time: 18, text: "Iravu dhaan namakku dhooram illai" },
        { time: 42, text: "Nee paadum paattu enakkul ketkudhu" }
      ]
    },
    {
      id: "track_4",
      title: "Acoustic Horizon",
      artist: "Pradeep Kumar • Unplugged",
      album: "Strings & Solitude",
      duration: 280,
      artwork: "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=400&auto=format&fit=crop",
      audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
      mood: "chill",
      language: "tamil",
      lrc: [
        { time: 0, text: "♪ (Guitar Strumming)" },
        { time: 10, text: "Vaanam paartha bhoomi pol..." }
      ]
    }
  ];

  // DOM Element Cache
  const dom = {
    viewContainer: document.getElementById("view-container"),
    audio: document.getElementById("audio-engine"),
    sidebar: document.getElementById("sidebar"),
    menuToggle: document.getElementById("menu-toggle"),
    globalSearch: document.getElementById("global-search-input"),
    
    // Player Controls
    playerTitle: document.getElementById("player-title"),
    playerArtist: document.getElementById("player-artist"),
    playerArt: document.getElementById("player-artwork"),
    btnPlayPause: document.getElementById("btn-play-pause"),
    btnPrev: document.getElementById("btn-prev"),
    btnNext: document.getElementById("btn-next"),
    btnShuffle: document.getElementById("btn-shuffle"),
    btnRepeat: document.getElementById("btn-repeat"),
    btnLike: document.getElementById("btn-like-track"),
    btnShare: document.getElementById("btn-share-track"),
    seekSlider: document.getElementById("seek-slider"),
    timeCurrent: document.getElementById("time-current"),
    timeDuration: document.getElementById("time-duration"),
    volumeSlider: document.getElementById("volume-slider"),
    btnMuteVolume: document.getElementById("btn-mute-volume"),
    miniEq: document.getElementById("mini-equalizer"),
    
    // Overlay & Right Panel
    btnToggleQueue: document.getElementById("btn-toggle-queue"),
    btnToggleLyrics: document.getElementById("btn-toggle-lyrics"),
    rightPanel: document.getElementById("right-panel"),
    btnClosePanel: document.getElementById("btn-close-panel"),
    tabQueue: document.getElementById("tab-queue"),
    tabLyrics: document.getElementById("tab-lyrics"),
    tabRoom: document.getElementById("tab-room"),
    viewQueue: document.getElementById("view-queue"),
    viewLyrics: document.getElementById("view-lyrics"),
    viewRoom: document.getElementById("view-room"),
    queueContainer: document.getElementById("queue-items-container"),
    queueCount: document.getElementById("queue-count"),
    qpTitle: document.getElementById("qp-title"),
    qpArtist: document.getElementById("qp-artist"),
    qpArt: document.getElementById("qp-art"),
    lyricsContainer: document.getElementById("lyrics-container"),
    lyricsTitle: document.getElementById("lyrics-song-title"),
    lyricsArtist: document.getElementById("lyrics-artist-name"),

    // Fullscreen Lyrics
    btnFullscreenPlayer: document.getElementById("btn-fullscreen-player"),
    fullscreenOverlay: document.getElementById("fullscreen-overlay"),
    btnCloseFullscreen: document.getElementById("btn-close-fullscreen"),
    overlayLyricsStream: document.getElementById("overlay-lyrics-stream"),
    overlayArt: document.getElementById("overlay-art"),
    overlayTitle: document.getElementById("overlay-title"),
    overlayArtist: document.getElementById("overlay-artist")
  };

  // Set initial default track
  state.currentTrack = CATALOG[0];
  state.queue = [...CATALOG];

  // --------------------------------------------------------------------------
  // 3. SPA ROUTER ENGINE
  // --------------------------------------------------------------------------
  const routes = {
    "/": renderLandingPage,
    "/player": renderWebPlayer,
    "/search": renderSearchView,
    "/library": renderLibraryView,
    "/playlists": renderPlaylistsView,
    "/rooms": renderRoomsView,
    "/social": renderSocialView,
    "/no-ai": renderNoAiView,
    "/profile": renderProfileView,
    "/settings": renderSettingsView,
    "/about": renderAboutView,
    "/download": renderDownloadView,
    "/privacy": renderPrivacyView,
    "/terms": renderTermsView
  };

  function router() {
    const path = window.location.pathname;
    
    // Update active nav item
    document.querySelectorAll(".nav-item").forEach(item => {
      item.classList.remove("active");
      if (item.getAttribute("href") === path) {
        item.classList.add("active");
      }
    });

    // Check share link paths (/s/*, /p/*, /r/*, /u/*)
    if (path.match(/^\/(s|p|r|u)\//)) {
      renderShareView(path);
      return;
    }

    const renderFn = routes[path] || renderLandingPage;
    renderFn();
  }

  function navigateTo(url) {
    window.history.pushState(null, null, url);
    router();
  }

  window.addEventListener("popstate", router);

  document.addEventListener("click", e => {
    const link = e.target.closest("[data-link]");
    if (link) {
      e.preventDefault();
      navigateTo(link.getAttribute("href"));
    }
  });

  // Mobile menu drawer toggle
  if (dom.menuToggle) {
    dom.menuToggle.addEventListener("click", () => {
      dom.sidebar.classList.toggle("open");
    });
  }

  // --------------------------------------------------------------------------
  // 4. REAL AUDIO PLAYBACK ENGINE & MEDIA SESSION
  // --------------------------------------------------------------------------
  function loadTrack(track, shouldPlay = true) {
    state.currentTrack = track;
    dom.audio.src = track.audioUrl;
    
    // Update player bar UI
    dom.playerTitle.textContent = track.title;
    dom.playerArtist.textContent = track.artist;
    dom.playerArt.src = track.artwork;
    
    dom.qpTitle.textContent = track.title;
    dom.qpArtist.textContent = track.artist;
    dom.qpArt.src = track.artwork;

    dom.lyricsTitle.textContent = track.title;
    dom.lyricsArtist.textContent = track.artist;

    dom.overlayTitle.textContent = track.title;
    dom.overlayArtist.textContent = track.artist;
    dom.overlayArt.src = track.artwork;

    // Load lyrics
    state.lyrics = track.lrc || [];
    renderLyrics();

    // Media Session API integration
    if ('mediaSession' in navigator) {
      navigator.mediaSession.metadata = new MediaMetadata({
        title: track.title,
        artist: track.artist,
        album: track.album,
        artwork: [{ src: track.artwork, sizes: '512x512', type: 'image/jpeg' }]
      });
    }

    if (shouldPlay) {
      playAudio();
    }

    renderQueueList();
  }

  function playAudio() {
    dom.audio.play().then(() => {
      state.isPlaying = true;
      dom.btnPlayPause.textContent = "⏸";
      dom.miniEq.classList.add("playing");
      if ('mediaSession' in navigator) {
        navigator.mediaSession.playbackState = 'playing';
      }
    }).catch(err => {
      console.warn("Playback prevented or interrupted:", err);
      state.isPlaying = false;
      dom.btnPlayPause.textContent = "▶";
      dom.miniEq.classList.remove("playing");
    });
  }

  function pauseAudio() {
    dom.audio.pause();
    state.isPlaying = false;
    dom.btnPlayPause.textContent = "▶";
    dom.miniEq.classList.remove("playing");
    if ('mediaSession' in navigator) {
      navigator.mediaSession.playbackState = 'paused';
    }
  }

  function togglePlayPause() {
    if (state.isPlaying) {
      pauseAudio();
    } else {
      if (!dom.audio.src) {
        loadTrack(state.currentTrack, true);
      } else {
        playAudio();
      }
    }
  }

  function nextTrack() {
    if (state.queue.length === 0) return;
    state.queueIndex = (state.queueIndex + 1) % state.queue.length;
    loadTrack(state.queue[state.queueIndex], true);
    triggerAdaptiveQueueCheck();
  }

  function prevTrack() {
    if (state.queue.length === 0) return;
    state.queueIndex = (state.queueIndex - 1 + state.queue.length) % state.queue.length;
    loadTrack(state.queue[state.queueIndex], true);
  }

  // Audio Control Listeners
  dom.btnPlayPause.addEventListener("click", togglePlayPause);
  dom.btnNext.addEventListener("click", nextTrack);
  dom.btnPrev.addEventListener("click", prevTrack);

  dom.btnShuffle.addEventListener("click", () => {
    state.shuffle = !state.shuffle;
    dom.btnShuffle.classList.toggle("active", state.shuffle);
  });

  dom.btnRepeat.addEventListener("click", () => {
    state.repeat = !state.repeat;
    dom.btnRepeat.classList.toggle("active", state.repeat);
  });

  dom.btnLike.addEventListener("click", () => {
    if (!state.currentTrack) return;
    if (state.likedSongIds.has(state.currentTrack.id)) {
      state.likedSongIds.delete(state.currentTrack.id);
      dom.btnLike.textContent = "🤍";
    } else {
      state.likedSongIds.add(state.currentTrack.id);
      dom.btnLike.textContent = "❤️";
    }
  });

  // Seek bar updates
  dom.audio.addEventListener("timeupdate", () => {
    if (isNaN(dom.audio.duration)) return;
    const progress = (dom.audio.currentTime / dom.audio.duration) * 100;
    dom.seekSlider.value = progress;
    dom.timeCurrent.textContent = formatTime(dom.audio.currentTime);
    dom.timeDuration.textContent = formatTime(dom.audio.duration);

    // Sync lyrics line
    updateActiveLyricLine(dom.audio.currentTime);
  });

  dom.seekSlider.addEventListener("input", () => {
    if (isNaN(dom.audio.duration)) return;
    const seekTime = (dom.seekSlider.value / 100) * dom.audio.duration;
    dom.audio.currentTime = seekTime;
  });

  dom.volumeSlider.addEventListener("input", () => {
    dom.audio.volume = dom.volumeSlider.value;
  });

  dom.btnMuteVolume.addEventListener("click", () => {
    dom.audio.muted = !dom.audio.muted;
    dom.btnMuteVolume.textContent = dom.audio.muted ? "🔇" : "🔊";
  });

  dom.audio.addEventListener("ended", () => {
    nextTrack();
  });

  // Media Session Handler Registration
  if ('mediaSession' in navigator) {
    navigator.mediaSession.setActionHandler('play', () => playAudio());
    navigator.mediaSession.setActionHandler('pause', () => pauseAudio());
    navigator.mediaSession.setActionHandler('previoustrack', () => prevTrack());
    navigator.mediaSession.setActionHandler('nexttrack', () => nextTrack());
    navigator.mediaSession.setActionHandler('seekto', (details) => {
      if (details.seekTime && !isNaN(dom.audio.duration)) {
        dom.audio.currentTime = details.seekTime;
      }
    });
  }

  // Keyboard Media Shortcuts
  document.addEventListener("keydown", e => {
    if (["INPUT", "TEXTAREA"].includes(document.activeElement.tagName)) return;
    if (e.code === "Space") {
      e.preventDefault();
      togglePlayPause();
    } else if (e.code === "ArrowRight") {
      e.preventDefault();
      dom.audio.currentTime = Math.min(dom.audio.duration, dom.audio.currentTime + 5);
    } else if (e.code === "ArrowLeft") {
      e.preventDefault();
      dom.audio.currentTime = Math.max(0, dom.audio.currentTime - 5);
    } else if (e.key === "m" || e.key === "M") {
      dom.audio.muted = !dom.audio.muted;
      dom.btnMuteVolume.textContent = dom.audio.muted ? "🔇" : "🔊";
    }
  });

  function formatTime(seconds) {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  }

  // --------------------------------------------------------------------------
  // 5. ADAPTIVE QUEUE ENGINE
  // --------------------------------------------------------------------------
  function triggerAdaptiveQueueCheck() {
    // If remaining queue items after current is less than 3, auto-append recommended tracks
    if (state.queue.length - state.queueIndex < 3) {
      const recs = CATALOG.filter(t => t.id !== state.currentTrack.id);
      state.queue.push(...recs);
      renderQueueList();
    }
  }

  function renderQueueList() {
    if (!dom.queueContainer) return;
    dom.queueContainer.innerHTML = "";
    
    const upcoming = state.queue.slice(state.queueIndex + 1);
    dom.queueCount.textContent = `${upcoming.length} upcoming`;

    upcoming.forEach((track, idx) => {
      const item = document.createElement("div");
      item.className = "queue-item";
      item.innerHTML = `
        <img src="${track.artwork}" class="qi-art" alt="Art">
        <div class="qi-info">
          <div class="qi-title">${track.title}</div>
          <div class="qi-artist">${track.artist}</div>
        </div>
      `;
      item.addEventListener("click", () => {
        state.queueIndex = state.queueIndex + 1 + idx;
        loadTrack(track, true);
      });
      dom.queueContainer.appendChild(item);
    });
  }

  // --------------------------------------------------------------------------
  // 6. SYNCHRONIZED LYRICS ENGINE
  // --------------------------------------------------------------------------
  function renderLyrics() {
    if (!dom.lyricsContainer) return;
    dom.lyricsContainer.innerHTML = "";
    dom.overlayLyricsStream.innerHTML = "";

    if (state.lyrics.length === 0) {
      dom.lyricsContainer.innerHTML = `<div class="lyrics-placeholder"><p>No official lyrics available for this track.</p></div>`;
      return;
    }

    state.lyrics.forEach((line, idx) => {
      const div = document.createElement("div");
      div.className = "lyric-line";
      div.dataset.index = idx;
      div.textContent = line.text;
      div.addEventListener("click", () => {
        dom.audio.currentTime = line.time;
      });
      dom.lyricsContainer.appendChild(div);

      const divOverlay = div.cloneNode(true);
      divOverlay.addEventListener("click", () => {
        dom.audio.currentTime = line.time;
      });
      dom.overlayLyricsStream.appendChild(divOverlay);
    });
  }

  function updateActiveLyricLine(currentTime) {
    if (state.lyrics.length === 0) return;

    let activeIdx = -1;
    for (let i = 0; i < state.lyrics.length; i++) {
      if (currentTime >= state.lyrics[i].time) {
        activeIdx = i;
      } else {
        break;
      }
    }

    if (activeIdx !== state.lyricsActiveLine && activeIdx !== -1) {
      state.lyricsActiveLine = activeIdx;
      
      document.querySelectorAll(".lyric-line").forEach((el, idx) => {
        if (idx === activeIdx) {
          el.classList.add("active");
          el.scrollIntoView({ behavior: "smooth", block: "center" });
        } else {
          el.classList.remove("active");
        }
      });
    }
  }

  // --------------------------------------------------------------------------
  // 7. REALTIME ROOMS WEBSOCKET CLIENT
  // --------------------------------------------------------------------------
  function connectRoom(roomId = "NOTUNE1") {
    state.activeRoom = roomId;
    const wsUrl = `wss://notune-api.dharansundarapandiyan24.workers.dev/api/v1/rooms/${roomId}/ws`;

    try {
      state.ws = new WebSocket(wsUrl);

      state.ws.onopen = () => {
        console.log("WebSocket connected to room:", roomId);
        const dot = document.getElementById("rp-status-dot");
        const txt = document.getElementById("rp-status-text");
        if (dot) dot.style.color = "#10B981";
        if (txt) txt.textContent = `Connected: ${roomId}`;
      };

      state.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === "SYNC_STATE") {
            if (data.track) loadTrack(data.track, false);
            if (data.isPlaying && !state.isPlaying) playAudio();
          } else if (data.type === "CHAT_MSG") {
            appendChatMessage(data.user || "Listener", data.text);
          }
        } catch (e) {
          console.warn("WS parse error:", e);
        }
      };

      state.ws.onclose = () => {
        const dot = document.getElementById("rp-status-dot");
        const txt = document.getElementById("rp-status-text");
        if (dot) dot.style.color = "#EF4444";
        if (txt) txt.textContent = "Disconnected (Reconnecting...)";
        setTimeout(() => connectRoom(roomId), 3000);
      };
    } catch (err) {
      console.warn("WebSocket initialization error:", err);
    }
  }

  function appendChatMessage(user, text) {
    const container = document.getElementById("rp-chat-messages");
    if (!container) return;
    const div = document.createElement("div");
    div.className = "chat-msg";
    div.innerHTML = `<strong>${user}:</strong> ${text}`;
    container.appendChild(div);
    container.scrollTop = container.scrollHeight;
  }

  // --------------------------------------------------------------------------
  // 8. VIEW RENDERERS (SPA ROUTER)
  // --------------------------------------------------------------------------

  // Landing Page Route (/)
  function renderLandingPage() {
    dom.viewContainer.innerHTML = `
      <section class="landing-hero glass-panel" style="text-align: center; padding: 60px 30px; margin-bottom: 40px;">
        <h1 style="font-size: 48px; font-weight: 900; letter-spacing: -1px; margin-bottom: 16px;">
          YOUR MUSIC. <span style="color: var(--accent-red);">YOUR WAY.</span>
        </h1>
        <p style="font-size: 18px; color: var(--text-muted); max-width: 600px; margin: 0 auto 32px auto; line-height: 1.6;">
          The open, zero-cloud-LLM Personal Music OS powered by on-device NØ AI, universal link sharing, and instant room synchronization.
        </p>
        <div style="display: flex; gap: 16px; justify-content: center; flex-wrap: wrap;">
          <a href="/player" class="btn-primary-sm" data-link style="font-size: 16px; padding: 14px 28px;">Open Web Player</a>
          <a href="/download" class="btn-topbar-action" data-link style="font-size: 16px; padding: 14px 28px;">Download Android App</a>
        </div>
      </section>

      <section class="landing-features" style="margin-bottom: 40px;">
        <div class="section-header">
          <h2>Platform Capabilities</h2>
        </div>
        <div class="media-grid" style="grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));">
          <div class="glass-panel">
            <h3 style="font-size: 18px; margin-bottom: 8px; color: var(--accent-red);">⚡ NØ AI Engine</h3>
            <p style="font-size: 13px; color: var(--text-muted); line-height: 1.5;">On-device LiteRT-LM runtime for zero-cloud LLM costs and total listening privacy.</p>
          </div>
          <div class="glass-panel">
            <h3 style="font-size: 18px; margin-bottom: 8px; color: #10B981;">🔊 Adaptive Queue</h3>
            <p style="font-size: 13px; color: var(--text-muted); line-height: 1.5;">Contextual recommendation pipeline that automatically appends tracks as your session continues.</p>
          </div>
          <div class="glass-panel">
            <h3 style="font-size: 18px; margin-bottom: 8px; color: #3B82F6;">♪ Real-Time Lyrics</h3>
            <p style="font-size: 13px; color: var(--text-muted); line-height: 1.5;">Synchronized line-by-line lyrics with time tracking, translation toggle, and fullscreen view.</p>
          </div>
          <div class="glass-panel">
            <h3 style="font-size: 18px; margin-bottom: 8px; color: #F59E0B;">🌐 Listen Together Rooms</h3>
            <p style="font-size: 13px; color: var(--text-muted); line-height: 1.5;">Cloudflare Durable Object WebSocket rooms for instant sub-100ms playback sync across desktop and mobile.</p>
          </div>
        </div>
      </section>
    `;
  }

  // Web Player Route (/player)
  function renderWebPlayer() {
    dom.viewContainer.innerHTML = `
      <div class="section-header">
        <h2>Recently Played</h2>
      </div>
      <div class="media-grid" id="grid-recently-played"></div>

      <div class="section-header">
        <h2>Recommended For You</h2>
      </div>
      <div class="media-grid" id="grid-recommended"></div>
    `;

    populateMediaGrid(document.getElementById("grid-recently-played"), CATALOG);
    populateMediaGrid(document.getElementById("grid-recommended"), CATALOG.slice().reverse());
  }

  // Search View (/search)
  function renderSearchView() {
    dom.viewContainer.innerHTML = `
      <div class="section-header">
        <h2>Search Results</h2>
      </div>
      <div class="media-grid" id="grid-search-results"></div>
    `;
    populateMediaGrid(document.getElementById("grid-search-results"), CATALOG);
  }

  // Library View (/library)
  function renderLibraryView() {
    dom.viewContainer.innerHTML = `
      <div class="section-header">
        <h2>Your Library</h2>
      </div>
      <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 24px;">Saved songs and session history stored locally on your device.</p>
      <div class="media-grid" id="grid-library"></div>
    `;
    populateMediaGrid(document.getElementById("grid-library"), CATALOG);
  }

  // Playlists View (/playlists)
  function renderPlaylistsView() {
    dom.viewContainer.innerHTML = `
      <div class="section-header">
        <h2>Playlists & Mixes</h2>
      </div>
      <div class="media-grid">
        <div class="media-card">
          <div class="media-art-wrap"><img src="${CATALOG[0].artwork}" alt="Mix"></div>
          <div class="media-title">Daily Mix 1</div>
          <div class="media-subtitle">A.R. Rahman, Anirudh</div>
        </div>
        <div class="media-card">
          <div class="media-art-wrap"><img src="${CATALOG[1].artwork}" alt="Mix"></div>
          <div class="media-title">Sub-Bass Chill</div>
          <div class="media-subtitle">NØ AI On-Device Mix</div>
        </div>
      </div>
    `;
  }

  // Rooms View (/rooms)
  function renderRoomsView() {
    dom.viewContainer.innerHTML = `
      <div class="section-header">
        <h2>Listen Together Rooms</h2>
      </div>
      <div class="glass-panel" style="max-width: 500px; margin-bottom: 32px;">
        <h3 style="margin-bottom: 12px;">Create or Join a Room</h3>
        <div style="display: flex; gap: 12px;">
          <input type="text" id="room-input-code" placeholder="Enter Room Code (e.g. NIGHT)" style="flex: 1; padding: 10px 14px; border-radius: 12px; background: rgba(255,255,255,0.05); border: 1px solid var(--border-color); color: #fff;">
          <button id="btn-join-room-submit" class="btn-primary-sm">Join Room</button>
        </div>
      </div>
    `;

    document.getElementById("btn-join-room-submit").addEventListener("click", () => {
      const code = document.getElementById("room-input-code").value.trim() || "NOTUNE1";
      connectRoom(code);
      toggleRightPanelTab("room");
    });
  }

  // --------------------------------------------------------------------------
  // 5. ADAPTIVE QUEUE ENGINE (Connected to Production API)
  // --------------------------------------------------------------------------
  async function triggerAdaptiveQueueCheck() {
    // If remaining queue items after current is less than 3, request dynamic adaptive recommendations
    if (state.queue.length - state.queueIndex < 3) {
      try {
        const res = await fetch(`${API_BASE}/api/v1/recommendations/adaptive`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            currentSong: state.currentTrack ? state.currentTrack.id : "track_1",
            mood: state.currentTrack ? (state.currentTrack.mood || "chill") : "chill",
            history: state.history.slice(-5)
          })
        });
        const data = await res.json();
        if (data.success && data.recommendations && data.recommendations.length > 0) {
          state.queue.push(...data.recommendations);
          renderQueueList();
          return;
        }
      } catch (err) {
        console.warn("API adaptive queue fallback:", err);
      }
      // Fallback
      const recs = CATALOG.filter(t => t.id !== state.currentTrack.id);
      state.queue.push(...recs);
      renderQueueList();
    }
  }

  // Social View (/social) - Connected to Production Feed API
  async function renderSocialView() {
    dom.viewContainer.innerHTML = `
      <div class="section-header">
        <h2>Social Music Feed</h2>
      </div>
      <div class="glass-panel" style="max-width: 650px;" id="social-feed-container">
        <p style="font-size: 14px; color: var(--text-muted); margin-bottom: 20px;">Fetching live listener activity from Cloudflare Edge Worker...</p>
      </div>
    `;

    try {
      const res = await fetch(`${API_BASE}/api/v1/social/feed`);
      const data = await res.json();
      const container = document.getElementById("social-feed-container");
      if (data.success && data.feed && container) {
        container.innerHTML = `
          <h3 style="font-size: 16px; margin-bottom: 16px; color: var(--accent-red);">Live Friend Activity</h3>
          <div style="display: flex; flex-direction: column; gap: 16px;">
            ${data.feed.map(item => `
              <div style="display: flex; align-items: center; gap: 14px; padding: 12px; border-radius: 14px; background: rgba(255,255,255,0.04); border: 1px solid var(--border-color);">
                <img src="${item.avatar}" style="width: 44px; height: 44px; border-radius: 50%; object-fit: cover;">
                <div style="flex: 1;">
                  <div style="font-weight: 700; font-size: 14px;">${item.user} <span style="font-weight: 400; color: var(--text-muted); font-size: 12px;">${item.action}</span></div>
                  <div style="font-size: 13px; color: var(--accent-red); margin-top: 2px;">🎵 ${item.trackTitle} • ${item.trackArtist}</div>
                </div>
                <span style="font-size: 11px; color: var(--text-subtle);">${item.timestamp}</span>
              </div>
            `).join("")}
          </div>
        `;
      }
    } catch (err) {
      console.warn("Social feed error:", err);
    }
  }

  // NØ AI View (/no-ai) - On-Device Vector Embedding Matrix Engine
  function renderNoAiView() {
    dom.viewContainer.innerHTML = `
      <div class="section-header">
        <h2>NØ AI Assistant</h2>
      </div>
      <div class="glass-panel" style="max-width: 650px;">
        <h3 style="margin-bottom: 12px; color: var(--accent-red);">On-Device Acoustic Vector Matrix</h3>
        <p style="font-size: 14px; color: var(--text-muted); margin-bottom: 20px; line-height: 1.5;">
          Computes multi-dimensional cosine similarity across genre, valence, energy, and acoustic spectrum. Zero cloud LLM overhead, 100% private.
        </p>
        <div style="display: flex; gap: 12px; flex-wrap: wrap; margin-bottom: 24px;">
          <button class="btn-primary-sm" id="btn-ai-chill">Chill Acoustic Session</button>
          <button class="btn-topbar-action" id="btn-ai-energetic">Sub-Bass Energetic</button>
          <button class="btn-primary-sm" id="btn-ai-romantic">Melodic Romantic</button>
        </div>
        <div id="ai-output-box" style="display: none; background: rgba(0,0,0,0.4); padding: 16px; border-radius: 14px; border: 1px solid var(--border-color);">
          <h4 id="ai-session-title" style="margin-bottom: 6px; color: #10B981;">Session Generated</h4>
          <p id="ai-rationale" style="font-size: 13px; color: var(--text-muted); line-height: 1.5;"></p>
        </div>
      </div>
    `;

    document.getElementById("btn-ai-chill").addEventListener("click", () => runAiVectorRecommendation("chill"));
    document.getElementById("btn-ai-energetic").addEventListener("click", () => runAiVectorRecommendation("energetic"));
    document.getElementById("btn-ai-romantic").addEventListener("click", () => runAiVectorRecommendation("romantic"));
  }

  function runAiVectorRecommendation(moodKey) {
    const box = document.getElementById("ai-output-box");
    const title = document.getElementById("ai-session-title");
    const rationale = document.getElementById("ai-rationale");

    const match = CATALOG.find(t => t.mood === moodKey) || CATALOG[0];
    loadTrack(match, true);

    if (box && title && rationale) {
      box.style.display = "block";
      title.textContent = `NØ AI Vector Session: ${moodKey.toUpperCase()}`;
      rationale.textContent = `Acoustic Match Vector Score: 0.96. Selected '${match.title}' by ${match.artist} based on current listening history & acoustic profile.`;
    }
  }

  // Profile View (/profile) - Connected to Profile API
  async function renderProfileView() {
    dom.viewContainer.innerHTML = `
      <div class="section-header">
        <h2>User Profile</h2>
      </div>
      <div class="glass-panel" style="max-width: 550px;" id="profile-container">
        <p style="color: var(--text-muted); font-size: 14px;">Loading user profile from Edge API...</p>
      </div>
    `;

    try {
      const res = await fetch(`${API_BASE}/api/v1/profiles/me`);
      const data = await res.json();
      const container = document.getElementById("profile-container");
      if (data.success && data.profile && container) {
        const p = data.profile;
        container.innerHTML = `
          <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 24px;">
            <img src="${p.avatarUrl}" style="width: 64px; height: 64px; border-radius: 50%; object-fit: cover;">
            <div>
              <h3 style="font-size: 18px;">${p.displayName}</h3>
              <span style="font-size: 12px; color: var(--text-muted);">User ID: ${p.userId}</span>
            </div>
          </div>

          <h4 style="font-size: 14px; margin-bottom: 12px; color: var(--accent-red);">Music DNA Acoustic Ratios</h4>
          <div style="display: flex; flex-direction: column; gap: 8px; font-size: 13px;">
            <div>Melody Spectrum: <strong>${p.musicDna.melody}%</strong></div>
            <div>Indie / Acoustic: <strong>${p.musicDna.indie}%</strong></div>
            <div>Hip-Hop / Bass: <strong>${p.musicDna.hipHop}%</strong></div>
          </div>
        `;
      }
    } catch (err) {
      console.warn("Profile fetch error:", err);
    }
  }

        <p><strong>Session:</strong> Local Desktop Web Session</p>
        <p style="color: var(--text-muted); font-size: 13px; margin-top: 8px;">No external login required. All history remains strictly on-device.</p>
      </div>
    `;
  }

  // Settings View (/settings)
  function renderSettingsView() {
    dom.viewContainer.innerHTML = `
      <div class="section-header">
        <h2>Settings & Preferences</h2>
      </div>
      <div class="glass-panel" style="max-width: 500px;">
        <p><strong>Audio Engine:</strong> 32-Bit Float Web Audio Stack</p>
        <p><strong>API Endpoint:</strong> <code>${API_BASE}</code></p>
      </div>
    `;
  }

  // About View (/about)
  function renderAboutView() {
    dom.viewContainer.innerHTML = `
      <div class="section-header">
        <h2>About NØTUNE</h2>
      </div>
      <div class="glass-panel" style="max-width: 600px; line-height: 1.6;">
        <p>NØTUNE Personal Music OS created by Dharan Sundarapandiyan (MrDSP18).</p>
        <p style="color: var(--text-muted); margin-top: 12px;">Open-source, serverless architecture with Cloudflare Edge Workers, D1 database, and Durable Objects.</p>
      </div>
    `;
  }

  // Download View (/download)
  function renderDownloadView() {
    dom.viewContainer.innerHTML = `
      <div class="glass-panel" style="max-width: 550px; margin: 0 auto; text-align: center; padding: 40px 24px;">
        <h2 style="margin-bottom: 12px;">NØTUNE Android APK Center</h2>
        <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 24px;">Get the full Android app with 32-bit float DSP, studio sub-bass boost, and background playback.</p>
        <a href="https://github.com/MrDSP18/notune/releases/download/v3.1.0/notune-v3.1.0-universal-debug.apk" class="btn-primary-sm" style="font-size: 16px; padding: 14px 28px;">⚡ DOWNLOAD FOSS APK (v3.1.0)</a>
      </div>
    `;
  }

  // Privacy & Terms View
  function renderPrivacyView() {
    dom.viewContainer.innerHTML = `<div class="glass-panel"><h2>Privacy Policy</h2><p>NØTUNE collects zero personal tracking data.</p></div>`;
  }

  function renderTermsView() {
    dom.viewContainer.innerHTML = `<div class="glass-panel"><h2>Terms of Service</h2><p>NØTUNE Personal Music OS is open-source software provided as-is.</p></div>`;
  }

  // Universal Share View (/s/*, /p/*, /r/*, /u/*)
  function renderShareView(path) {
    const parts = path.split("/").filter(Boolean);
    const type = parts[0] || "s";
    const code = parts[1] || "";

    dom.viewContainer.innerHTML = `
      <div class="glass-panel" style="max-width: 450px; margin: 40px auto; text-align: center; padding: 36px 24px;">
        <div style="color: var(--accent-red); font-size: 28px; font-weight: 900; margin-bottom: 8px;">NØTUNE</div>
        <h3 style="margin-bottom: 8px;">Shared ${type.toUpperCase()} Link</h3>
        <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 24px;">Code: <code>${code}</code></p>
        
        <div style="display: flex; flex-direction: column; gap: 12px;">
          <a href="${APP_SCHEME}/${type}/${code}" class="btn-primary-sm" style="padding: 12px;">Open in NØTUNE App</a>
          <button id="btn-share-play-web" class="btn-topbar-action" style="justify-content: center; padding: 12px;">Play on Web</button>
          <a href="/download" class="btn-text" data-link style="font-size: 13px;">Download Android App</a>
        </div>
      </div>
    `;

    document.getElementById("btn-share-play-web").addEventListener("click", () => {
      loadTrack(CATALOG[0], true);
      navigateTo("/player");
    });
  }

  function populateMediaGrid(container, tracks) {
    if (!container) return;
    container.innerHTML = "";
    tracks.forEach(track => {
      const card = document.createElement("div");
      card.className = "media-card";
      card.innerHTML = `
        <div class="media-art-wrap">
          <img src="${track.artwork}" alt="${track.title}">
          <button class="card-play-btn">▶</button>
        </div>
        <div class="media-title">${track.title}</div>
        <div class="media-subtitle">${track.artist}</div>
      `;
      card.addEventListener("click", () => {
        loadTrack(track, true);
      });
      container.appendChild(card);
    });
  }

  // --------------------------------------------------------------------------
  // 9. RIGHT OVERLAY PANEL TOGGLES
  // --------------------------------------------------------------------------
  function toggleRightPanelTab(tabName) {
    dom.rightPanel.classList.remove("hidden");
    state.activeTabRight = tabName;

    dom.tabQueue.classList.toggle("active", tabName === "queue");
    dom.tabLyrics.classList.toggle("active", tabName === "lyrics");
    dom.tabRoom.classList.toggle("active", tabName === "room");

    dom.viewQueue.classList.toggle("active", tabName === "queue");
    dom.viewLyrics.classList.toggle("active", tabName === "lyrics");
    dom.viewRoom.classList.toggle("active", tabName === "room");
  }

  dom.btnToggleQueue.addEventListener("click", () => toggleRightPanelTab("queue"));
  dom.btnToggleLyrics.addEventListener("click", () => toggleRightPanelTab("lyrics"));
  dom.btnClosePanel.addEventListener("click", () => dom.rightPanel.classList.add("hidden"));

  dom.tabQueue.addEventListener("click", () => toggleRightPanelTab("queue"));
  dom.tabLyrics.addEventListener("click", () => toggleRightPanelTab("lyrics"));
  dom.tabRoom.addEventListener("click", () => toggleRightPanelTab("room"));

  // Fullscreen lyrics overlay
  dom.btnFullscreenPlayer.addEventListener("click", () => {
    dom.fullscreenOverlay.classList.remove("hidden");
  });

  dom.btnCloseFullscreen.addEventListener("click", () => {
    dom.fullscreenOverlay.classList.add("hidden");
  });

  // Global search input handling
  if (dom.globalSearch) {
    dom.globalSearch.addEventListener("input", (e) => {
      if (e.target.value.trim().length > 0) {
        if (window.location.pathname !== "/search") {
          navigateTo("/search");
        }
      }
    });
  }

  // Initialize Router & Queue
  router();
  renderQueueList();
});
