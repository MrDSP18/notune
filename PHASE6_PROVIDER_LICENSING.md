# NØTUNE Phase 6 — Metadata Provider Licensing & Redistribution Assessment

> **Date**: October 5, 2026
> **App Version**: NØTUNE v3.2
> **Scope**: Legal & API Terms Analysis for Universal Music Metadata Enrichment Stack

---

## Executive Summary

NØTUNE's architecture enforces a **Backend Metadata Gateway** pattern. All metadata queries pass through NØTUNE's Cloudflare Worker proxy (`https://notune-api.dharansundarapandiyan24.workers.dev`). This document establishes the licensing terms, attribution requirements, caching restrictions, artwork policies, and redistribution constraints for all integrated metadata providers.

---

## Provider License & Terms Matrix

| Provider | Data License / Terms | Attribution Required | Client-Side Secret Key Required | Caching Permitted | Artwork Rights | Redistribution Policy | Suitability Status |
|---|---|---|---|---|---|---|---|
| **MusicBrainz** | CC0 (Public Domain Data) | Recommended (`User-Agent` header) | **NO** | **YES** (unlimited) | Indirect (Cover Art Archive) | Free & Open | **APPROVED (Primary)** |
| **LRCLIB** | Open Public Lyrics API | Recommended | **NO** | **YES** | N/A (Lyrics text) | Free / Open Access | **APPROVED (Lyrics)** |
| **Last.fm API** | Non-Commercial / API Terms | Mandatory ("Powered by Last.fm") | **Server-Side Only** | **YES** (Up to 7 days cache) | Restricted (use official URLs) | Non-Commercial Only | **APPROVED (Proxy Gated)** |
| **TMDb (The Movie Database)** | TMDb API Terms of Use | Mandatory (TMDb Logo + Attribution) | **Server-Side Only** | **YES** | Permitted via TMDb CDN | Non-Commercial App | **APPROVED (Movie Context)** |
| **InnerTube / YouTube** | YouTube Terms of Service | Mandatory (YouTube branding) | **NO** (Public InnerTube) | **NO** (Do not cache video streams; artwork thumb cache allowed) | Thumbnail URLs only | Public API constraints | **APPROVED (Thumbnails Only)** |

---

## Detailed Provider Analysis

### 1. MusicBrainz (MetaBrainz Foundation)
- **Data License**: Creative Commons Zero (CC0 1.0 Universal). All core metadata (artists, recordings, releases, ISRCs, relationships) is public domain.
- **Attribution**: Requires a descriptive `User-Agent` header (e.g. `NoTuneApp/3.2.0 ( contact@notune.app )`).
- **Rate Limits**: 1 request per second per IP (handled server-side with worker queue and D1 cache).
- **Caching & Storage**: Permitted without restriction.
- **Verdict**: **APPROVED**. Serves as NØTUNE's primary canonical song identity provider.

### 2. LRCLIB (Open Lyrics API)
- **Data License**: Public API service for crowd-sourced and open synchronized lyrics.
- **Attribution**: Recommended user agent declaration.
- **Caching**: Local and server-side caching permitted to reduce API load.
- **Verdict**: **APPROVED**. Primary provider for synced `.lrc` and plain text lyrics.

### 3. Last.fm API
- **Terms of Service**: Requires registration of API key. Prohibits selling or commercial licensing of Last.fm data.
- **Attribution**: Must display "Powered by Last.fm" attribution when presenting artist bio or top tag data.
- **Key Safety**: Key is strictly stored in Cloudflare Worker secrets (`env.LASTFM_API_KEY`) and **never exposed to Android APK**.
- **Caching**: Permitted for performance optimization up to 7 days per item.
- **Verdict**: **APPROVED** for server-side enrichment of artist biographies, tags, genres, and mood vectors.

### 4. TMDb (The Movie Database)
- **Terms of Service**: Requires API Key for searching movie details, cast, and crew.
- **Attribution**: App must display TMDb logo and attribution statement ("This product uses the TMDb API but is not endorsed or certified by TMDb").
- **Key Safety**: Key stored exclusively in Cloudflare Worker secrets (`env.TMDB_API_KEY`).
- **Verdict**: **APPROVED** for enriching movie soundtracks, directors, music directors, lead actors, cast, and crew.

### 5. InnerTube / YouTube Music
- **Terms of Service**: Public thumbnail images (`https://i.ytimg.com/vi/<id>/hqdefault.jpg`) may be rendered in media player UI.
- **Restrictions**: Audio stream URLs must not be saved permanently or redistributed outside the player pipeline.
- **Verdict**: **APPROVED** for high-resolution artwork and public metadata context.

---

## Compliance Directives for NØTUNE Android App

1. **No Scraping**: NØTUNE does not perform HTML web scraping. All integrations utilize official REST / JSON endpoints.
2. **Attribution Display**: The `SongDetailsScreen` and `AboutScreen` in NØTUNE display required metadata attribution badges (MusicBrainz, TMDb, Last.fm, LRCLIB).
3. **Zero Key Leakage**: No provider API keys exist in the Android source code or compiled DEX binaries.
