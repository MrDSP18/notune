# NØTUNE Live Multilingual Lyrics Architecture

## Overview

The **NØTUNE Live Multilingual Lyrics Engine** provides real-time, synchronized lyrics rendering, script-aware transliteration/romanization, and line-preserving translation across 25+ world languages with a strict **Zero-Hallucination Guarantee**.

---

## Core Principles & Pipeline

```text
Track Selected $\rightarrow$ LyricsResolver (Synced Provider $\rightarrow$ Plain Provider $\rightarrow$ Cache $\rightarrow$ User)
                           │
                           ├── If Found: LyricsSyncEngine (Media3 Position Sync)
                           │                │
                           │                ├── Original Script
                           │                ├── Romanized / Phonetic Script
                           │                ├── Translated Meaning
                           │                └── Dual & Triple View Stacks
                           │
                           └── If Not Found: "Lyrics unavailable" Fallback State
```

> [!IMPORTANT]
> **Zero-Hallucination Rule**: AI models will **never** generate, hallucinate, or fabricate missing copyrighted song lyrics. If no authorized or user-provided lyrics exist, NØTUNE displays "Lyrics unavailable".

---

## Core Components

1. **`LyricsLanguageRegistry`**:
   - Manages metadata for 25+ languages (Latin, Tamil, Telugu, Kannada, Malayalam, Hindi, Bengali, Marathi, Gujarati, Punjabi, Urdu, Arabic, Japanese, Korean, etc.).
   - Specifies script direction, transliteration, and translation capabilities.

2. **`LyricsResolver`**:
   - Provider-agnostic lyrics resolution.
   - Evaluates sources in priority order: Synced Provider $\rightarrow$ Plain Provider $\rightarrow$ Cached Authorized $\rightarrow$ Community User $\rightarrow$ Unavailable.

3. **`LyricsSyncEngine`**:
   - Synchronizes playback position with `LyricLine` timestamps.
   - Calculates line-level progress interpolation and word-level highlighting.
   - Re-synchronizes position automatically on seek, pause, resume, or track change.

4. **`LyricsStudioContainer`**:
   - Compose container rendering Original, Pronunciation, Meaning, Dual, and Triple View stacks.
