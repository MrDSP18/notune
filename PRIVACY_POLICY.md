# NØTUNE Privacy Policy

**Last updated: September 19, 2026**

NØTUNE is a local-first Android music player. The app does not sell personal data or use advertising trackers. This document describes the optional network features accurately; distribution-specific legal review is still recommended before launch.

## On-device data

Your local music library, playback queue, favorites, listening history, downloads, customization, and Music DNA are stored on your device. They are not uploaded by the local player. Android permissions are requested only when a selected feature needs them.

## Optional network requests

Search, streaming, artwork, lyrics, recognition, and optional AI features send the minimum request needed to the provider selected by you. Provider policies apply to those requests. If you configure an AI provider, prompts and any content included in them go directly to that provider; NØTUNE does not claim ownership of or retain those prompts on its own servers.

The optional NØTUNE cloud/social backend can process an account identifier, display name, avatar, social posts, messages, friendship data, presence, and synchronization data that you choose to submit. Cloud features are not required for local playback. Presence and social sharing can be disabled in the app.

## Credentials

API keys are intended to remain in the app's protected local storage and are sent only to the provider they belong to. Never paste secrets into issues, builds, or public configuration. Cloud credentials are server-side secrets and are not included in the Android app.

## Retention and deletion

Local data can be removed by clearing the app data or using the app's data controls. For cloud/social data, use the account deletion and content deletion controls when available, or contact the project through a private GitHub security/support channel. Backups and provider retention are governed by the applicable provider policy.

## Third parties

Depending on enabled features, requests may involve YouTube/InnerTube-compatible services, lyric providers, artwork CDNs, optional AI providers, Last.fm, Shazam, Discord, Google Drive, or Cloudflare/Render infrastructure. The FOSS build excludes Google Play Services features.

## Contact

For privacy questions, open a repository discussion or contact the maintainer. For vulnerabilities, follow [SECURITY.md](SECURITY.md) and do not publish secrets in a public issue.
