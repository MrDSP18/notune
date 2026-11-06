# Security Policy

## Supported versions

| Version | Supported |
| --- | --- |
| `1.2.x` | Yes |
| Older versions | No |

## Reporting a vulnerability

Please do **not** open a public issue for a security vulnerability. Use a GitHub private security advisory in this repository or email `security@notune.fun`. Include the affected version, reproduction steps, impact, and (if available) a suggested fix. We aim to acknowledge reports within 7 days.

## Production security guarantees

- No production credentials, signing keys, database URLs, or object-storage keys belong in Git.
- The backend refuses to start in production when `JWT_SECRET` or `DATABASE_URL` is missing or when the JWT secret is too short.
- API responses do not expose database or provider error messages.
- CORS is allow-list based (`CORS_ORIGINS`); it is not an unrestricted wildcard.
- Authentication is required for private/social operations and WebSocket authentication uses a signed JWT rather than a user-supplied user ID.
- Request bodies are size-limited and common security headers are enabled without collecting analytics.
- Local-first music, history, and credentials remain on-device unless the user explicitly enables a cloud feature.

## Developer checklist

- Store secrets in GitHub Actions/Render secret variables or local ignored files.
- Rotate any credential that has ever appeared in repository history.
- Review permissions before release; microphone, location, and media access must be feature-gated and user-approved.
- Run `./gradlew lintUniversalFossDebug testUniversalFossDebugUnitTest` and `npm audit --omit=dev` before release.
- Keep release signing keys outside the repository and use a dedicated release keystore.

## Sensitive files

Never commit `google-services.json`, `local.properties`, `*.keystore`, `*.jks`, `.env`, API tokens, or Cloudflare R2 credentials. If one is exposed, revoke it immediately; deleting the file is not enough because Git history remains.

## Privacy

NØTUNE is local-first and does not sell personal data. Cloud/social features are optional. Users should be able to delete their cloud account and content through the product; providers used for playback, artwork, lyrics, or optional AI receive only the requests required for the selected feature. See [PRIVACY_POLICY.md](PRIVACY_POLICY.md).
