# NØTUNE — Observability Gaps & Production Telemetry Plan

**Date**: 2026-10-05  
**Scope**: Cloudflare Worker Gateway (`notune-api`) & Android Client Telemetry  

---

## 1. Overview

NØTUNE operates on a privacy-first design model. No telemetry or analytics SDKs are embedded in the Android client DEX binary. All production observability must be collected server-side at the Cloudflare Edge Gateway without storing end-user IP addresses or personal identifiers.

---

## 2. Identified Concrete Observability Gaps

| ID | Area | Current State | Target Observability | Priority |
| :--- | :--- | :--- | :--- | :--- |
| **OBS-01** | Upstream Provider Latency | Monitored in aggregate logs only | Per-provider (MusicBrainz, TMDb, Last.fm, LRCLIB) latency histograms & 95th percentile metrics | P1 |
| **OBS-02** | Metadata Cache Hit Rate | Local memory cache only | Cloudflare KV / D1 cache hit ratio vs upstream cache miss tracking | P1 |
| **OBS-03** | Upstream Rate Limit Warnings | Logged on HTTP 429 response | Proactive rate-limit budget tracking & alert triggers prior to HTTP 429 | P1 |
| **OBS-04** | Room Disconnect Telemetry | Client-side reconnect loop | Server-side WebSocket connection drop rate & Durable Object session duration statistics | P2 |
| **OBS-05** | Fallback Activation Frequency | Monitored client-side via Timber | Aggregated server-side count of metadata fallback responses due to low confidence | P2 |

---

## 3. Privacy-Compliant Implementation Rules

1. **Zero Client Tracking**: Never add Google Analytics, Firebase Analytics, Mixpanel, or third-party client trackers.
2. **Server-Side Anonymization**: Cloudflare Worker request logs must strip `CF-Connecting-IP`, `User-Agent`, and authorization tokens before logging.
3. **Structured Log Format**: All Worker logs must format telemetry as JSON to allow automated aggregation via Cloudflare Logpush.
