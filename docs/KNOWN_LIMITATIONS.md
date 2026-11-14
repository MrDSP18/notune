# NØTUNE — Known Limitations & Operational Considerations

This document transparently records technical trade-offs, Android platform constraints, and external dependency requirements for NØTUNE.

---

## 1. Physical Device & Hardware Dependencies

1. **Bluetooth Codec Switching**:
   - Media button receiving and intent dispatching are implemented and verified via unit tests; however, physical hardware codec switching (LDAC, aptX HD, AAC negotiation) is managed by the Android operating system and vendor HAL.
2. **Audio Effects Compatibility**:
   - Device-specific DSP effects (Virtualizer and Bass Boost) rely on Android `AudioEffect` framework support, which varies according to OEM hardware and manufacturer DSP implementations.
3. **Android Keystore Hardware Backing**:
   - AES-256-GCM encryption is backed by hardware Secure Elements or StrongBox on devices running Android 9+ with compliant hardware. On devices lacking hardware security modules, Android KeyStore falls back to software-emulated TEE protection.

---

## 2. Storage & Scoped Storage Permissions (Android 10+)

1. **Physical Tag Modification on External Storage**:
   - While `LocalMetadataEditor.kt` updates the Room database atomically and accurately, writing ID3 tags directly into external media files on Android 10+ (API 29+) requires user consent via `MediaStore.createWriteRequest` or SAF folder authorization. NØTUNE's contract accurately distinguishes between database synchronization and physical disk modifications.
2. **SAF Media Scanning Performance**:
   - Scanning vast music libraries over Storage Access Framework (SAF) Document Trees has higher I/O overhead compared to MediaStore queries due to IPC binder boundaries.

---

## 3. External API & Streaming Limits

1. **InnerTube YouTube Streaming**:
   - Streaming playback depends on YouTube Music endpoints and cipher extraction. Video format availability may be subject to geographic restrictions and YouTube bot-detection mechanisms (PoToken).
2. **Cloud AI Assistant Rate Limits**:
   - Cloud AI providers (Groq, Gemini, OpenRouter) require user-configured API keys or server quotas. When offline or unconfigured, NØTUNE falls back to local heuristic providers (`NoTuneBasicAiProvider`).
