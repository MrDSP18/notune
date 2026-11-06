# NØTUNE — STITCH & TUNE MUSIC OS REFERENCE UI MAP

**Reference Path**: `/home/dharan-25486/Documents/music/V2/notune/stitch_n_tune_music_os`  
**Primary Visual Source**: Reference Mockup Image & HTML/CSS specifications (`design.md`, `code.html`)

---

## 1. VISUAL REFERENCE MAP STRUCTURE

```text
REFERENCE
│
├── Overall Layout: High-contrast OLED Pitch Black (#131313, #09090B)
├── Header: Live Telemetry Status Bar (SYS.TIME, KERNEL:ACTIVE, SESSION ID)
├── Navigation: Persistent Bottom Bar with active Crimson Accent (#FF002E)
├── Home: Vibe Radar Biosensory Card + Master Audio Card + Pinned Widgets
├── Music Cards: 1dp Hairline Border (#27272A) Cards with Format Chips (96kHz, FLAC 24-BIT, DSD)
├── Player: Integrated Waveform Visualizer + Hi-Res Audio Telemetry Matrix
├── Search: Natural Language Query Terminal with Filter Chips
├── Library: DAC & Offline Audio Storage Matrix
├── Rooms: Low-Latency Room Sync Telemetry (SYNC: 1.2ms, LATENCY 14ms)
├── AI DJ: Command Terminal Header (SYSTEM // AI_DJ_INTELLIGENCE_v2.0.0)
├── Typography: Space Grotesk (Headlines), JetBrains Mono (Code/Telemetry), Inter (Body)
├── Icons: Material Symbols Outlined with optical 20px sizing
├── Colors: Crimson Vivid (#FF002E), Pure White (#FFFFFF), Surface (#131313), Zinc Deep (#09090B), Cyan (#69D6E2)
├── Surfaces: 1dp Hairline Border Cards, Backdrop Blur (20px), OLED Pitch Black
├── Borders: Hairline #27272A
├── Shadows: Subtle Black Elevation [0 1px 8px rgba(0,0,0,0.5)]
├── Artwork: Corner format chips (96kHz, DSD, FLAC 24-BIT)
├── Animations: Pulse wave animation on live telemetry LED indicators
├── Spacing: 1rem margins, 0.75rem getters, 0.25rem-1.5rem padding steps
└── Responsive Behavior: Adaptive column layout for mobile, tablet, and wide screens
```

---

## 2. KEY COLOR & TYPOGRAPHY TOKENS

### Color Tokens
- `background` / `surface`: `#131313`
- `zinc-deep` (Card Surface): `#09090B`
- `zinc-dark`: `#18181B`
- `border-hairline`: `#27272A`
- `crimson-vivid` (Primary): `#FF002E`
- `tertiary` (Cyan Accent): `#69D6E2`
- `on-surface` / `pure-white`: `#FFFFFF` / `#E2E2E2`

### Typography Tokens
- `headline-md` / `display-lg`: `Space Grotesk`
- `meta-mono` / `label-code`: `JetBrains Mono`
- `body-lg` / `body-md`: `Inter`
