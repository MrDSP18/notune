# NØTUNE Design System & Visual Architecture

## 1. Design Philosophy
NØTUNE's design architecture is built around dynamic visual expression, typographic precision, and structural adaptability. Rather than simple color swaps, themes represent complete **Design Languages** (`NoTuneThemeEngine`) that adapt visual surfaces, geometry, borders, typography fallbacks, and animation dynamics.

---

## 2. Core Tokens & Utilities
- **Color System**: Primary, Secondary, Tertiary accents + Light & Dark Material3 ColorSchemes.
- **Typography Engine**: `NoTuneTypographySystem` supporting font style scaling and script-aware font fallbacks (Latin, Devanagari, Tamil, Kannada, Telugu, Malayalam, Bengali, Gujarati, Gurmukhi, Odia, Perso-Arabic).
- **Artwork Color Extraction**: `generateThemeFromArtwork(bitmap)` extracts vibrant and muted tones directly from album covers using AndroidX Palette for dynamic track-based UI adaptation.
- **Logo Variants**: 12 custom vector canvas logo implementations (`NoTuneLogoSystem`).
