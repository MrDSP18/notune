// NØTUNE Web Player & Universal Link Router Client Logic

document.addEventListener("DOMContentLoaded", () => {
  const shareCodeInput = document.getElementById("share-code-input");
  const resolveBtn = document.getElementById("resolve-btn");
  const playBtn = document.getElementById("play-btn");
  const trackTitle = document.getElementById("track-title");
  const trackArtist = document.getElementById("track-artist");
  const openAppBtn = document.getElementById("open-app-btn");
  const roomCodeDisplay = document.getElementById("room-code-display");

  // Default track metadata
  let currentTrack = {
    title: "Hosanna",
    artist: "A.R. Rahman • Wind (Tamil)",
    audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
    code: "8F92K"
  };

  const audio = document.getElementById("audio-element");
  let isPlaying = false;

  // Resolve button handler
  resolveBtn.addEventListener("click", () => {
    const rawVal = shareCodeInput.value.trim();
    if (!rawVal) return;

    let code = rawVal.replace(/^https?:\/\/[^\/]+\//, "");
    code = code.replace(/^[spru]\//, "");

    resolveShareCode(code);
  });

  function resolveShareCode(code) {
    trackTitle.textContent = `Resolving Link [${code}]...`;
    trackArtist.textContent = "NØTUNE Edge Worker Router";
    openAppBtn.href = `notune://open/s/${code}`;
    roomCodeDisplay.textContent = code;

    setTimeout(() => {
      trackTitle.textContent = `Track ${code.toUpperCase()}`;
      trackArtist.textContent = "NØTUNE Shared Session Seed";
    }, 600);
  }

  // Play/Pause button handler
  playBtn.addEventListener("click", () => {
    if (isPlaying) {
      audio.pause();
      playBtn.textContent = "▶";
      isPlaying = false;
    } else {
      audio.src = currentTrack.audioUrl;
      audio.play().then(() => {
        playBtn.textContent = "⏸";
        isPlaying = true;
      }).catch(err => {
        console.warn("Audio playback error:", err);
        playBtn.textContent = "▶";
      });
    }
  });

  // Check URL query parameters for direct link resolution (e.g. ?code=8F92K)
  const urlParams = new URLSearchParams(window.location.search);
  const paramCode = urlParams.get("code");
  if (paramCode) {
    resolveShareCode(paramCode);
  }
});
