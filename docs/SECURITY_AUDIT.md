# NØTUNE — Application Security Audit & Hardening Specification

**Target:** NØTUNE Android Application  
**Standard:** OWASP Mobile Application Security Verification Standard (MASVS) & Android Platform Security Best Practices  
**Status:** COMPLETED & HARDENED  

---

## 1. Threat Modeling & Vulnerability Findings

| Vulnerability Category | Original Risk | Remediation Implemented | Verification Evidence |
| :--- | :--- | :--- | :--- |
| **Credential Infiltration in Bytecode** | Secrets (`GH_CLIENT_SECRET`, `FLOW_NEURO_API_KEY`, etc.) baked into `BuildConfig` fields in `app/build.gradle.kts`. | Stripped confidential keys from BuildConfig. All runtime keys delegated to KeyStore-backed `SecureStorageManager`. | DEX bytecode search verifies 0 hardcoded keys. |
| **Storage Path Traversal via FileProvider** | `provider_paths.xml` exposed root `<external-path path="." />`. | Scoped to `<external-files-path>` and `<files-path>` subdirectories only. | Manifest and XML inspection. |
| **WebView Local File Disclosure** | `PoTokenWebView.kt` allowed file access, content access, and cross-origin file URL access. | Disabled `allowFileAccess`, `allowContentAccess`, `allowFileAccessFromFileURLs`, `allowUniversalAccessFromFileURLs`. | Source inspection and unit tests. |
| **OAuth Intent Scheme Hijacking** | `DiscordOAuthCallbackActivity` accepted untrusted deep link intents without strict scheme verification. | Added strict scheme verification matching `${discordRedirectScheme}` and state validation. | Unit test validation. |
| **PII / Secret Leakage via AI Assistant** | User chat and lyrics sent to external LLMs contained raw emails, phone numbers, IP addresses, or payment cards. | `AIRequestSanitizer` regex filter strips and replaces sensitive PII with `[REDACTED_*]` tokens before transmission. | `DuplicateSongDetectorUnicodeTest.testSanitizerRedactsEmailsPhoneNumbersAndSecrets`. |
| **Unauthenticated File Modification Claims** | Metadata editor falsely reported physical audio files updated when only Room database was modified. | Introduced `updateSongMetadataDetailed` and `bulkUpdateSongMetadata` with explicit contract reporting. | `LocalMetadataEditorTest.kt`. |
| **CI/CD Privilege Escalation** | `android-build.yml` ran with `contents: read` while attempting to push GitHub Releases, risking broken workflows. | Separated CI build/test (`contents: read`) from preview release publishing (`contents: write`). | Workflow validation. |

---

## 2. Cryptographic Architecture

### Android KeyStore Master Key
- **Alias:** `notune_master_keystore_key` / `NOTUNE_CREDENTIALS_KEY`
- **Algorithm:** AES-256-GCM (`KeyProperties.KEY_ALGORITHM_AES`, `BLOCK_MODE_GCM`, `ENCRYPTION_PADDING_NONE`)
- **Key Storage:** Hardware-backed Secure Element / StrongBox when available on supported devices.
- **Initialization Vector (IV):** Cryptographically secure random 12-byte IV generated per encryption invocation.
- **Authentication Tag:** 128-bit authentication tag appended to ciphertext to prevent tampering.

---

## 3. Network & Transport Security

1. **Cleartext Traffic Policy:** Blocked across application manifest (`android:usesCleartextTraffic="false"`).
2. **TLS Configuration:** Enforced TLS 1.3/1.2 with standard certificate validation.
3. **API Headers:** Cloud synchronization requests transmit version headers (`X-Sync-Protocol-Version: 1.0`) and UUID-backed idempotency tokens (`X-Idempotency-Key`).
