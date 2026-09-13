# NØTUNE Lyrics Provider Compliance Audit

## 1. Provider Reality & Terms Audit

The table below outlines the status of lyrics providers integrated into NØTUNE.

| Provider | Source Module | API Method | Licensing Status | Caching Permitted | Attribution Requirement | Production Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **LrcLib** | `:lrclib` | REST API (`lrclib.net`) | Open / User-contributed LRC database | Yes (Local cache permitted) | Required ("Powered by LRCLIB") | `ENABLED` |
| **KuGou** | `:kugou` | REST API (Public candidate search) | Publicly accessible metadata | Local memory cache only | Required | `ENABLED` |
| **Paxsenix** | `:paxsenixlyrics` | REST API | Third-party lyrics endpoint | Temporary session cache | Required | `ENABLED` |
| **BetterLyrics**| `:betterlyrics` | Parser | Local synced lyrics parser | Yes | N/A | `ENABLED` |

---

## 2. Translation vs. Transliteration Rules
- **Translation**: Converts lyrics text into the semantic meaning of the target language (e.g. English "Until I found you" → Tamil "நான் உன்னை கண்டுபிடிக்கும் வரை").
- **Transliteration (Sing-Along)**: Converts lyrics text into phonetic script pronunciation (e.g. English "Until I found you" → Tamil "அன்டில் ஐ ஃபவுண்ட் யூ").
- **Timestamp Preservation**: All transformations preserve `startTimeMs` and `endTimeMs` synchronized line boundaries without modification.
