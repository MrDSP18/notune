# NØTUNE — Quality Assurance & Test Automation Strategy

## 1. Testing Philosophy

The testing strategy follows the standard testing pyramid with high-density unit tests for domain logic and signal processing, Robolectric tests for Android-specific components, and mocked service layers for external HTTP and database interactions.

---

## 2. Test Suites & Verification Matrix

### Core Domain & Database (`:core`)
- **`DatabasePersistenceTest.kt`**: Validates Room database creation, entity persistence, transaction consistency, and clean teardown across restarts.
- **`MediaItemExtTest.kt`**: Tests `MediaItem.safeMetadata` defensive fallback behavior when external media items have null or malformed extras.

### Audio Signal Processing (`:playback`)
- **`BiquadFilterTest.kt`**: Validates mathematical stability, peak/notch/shelf filter calculations, Nyquist frequency protection, and zero-sample-rate bounds.
- **`AudioProcessorValidationTest.kt`**: Tests `CustomEqualizerAudioProcessor`, `AutomixDuckAudioProcessor`, `StereoWidenerAudioProcessor`, and `SilenceDetectorAudioProcessor` against unsupported channel configurations, negative sample rates, and empty PCM buffers.

### Application Logic & Local Media (`:app`)
- **`DuplicateSongDetectorUnicodeTest.kt`**: Tests Unicode normalization (`\p{L}\p{M}\p{N}`) for Indic scripts (Tamil, Hindi), non-Latin key uniqueness, and PII sanitization.
- **`LocalMediaAlgorithmsTest.kt`**: Tests smart playlist limits, artist diversification, zero-play-history handling, and Unicode song normalization.
- **`LocalMetadataEditorTest.kt`**: Tests single and bulk metadata editing, transactional rollback on error, and status reporting contract.
- **`FlowCandidateScorerTest.kt`**: Tests context-aware recommendation scoring, session language tracking, and Music DNA affinity.
- **`AiToolExecutionTest.kt`**: Tests tool execution for playback commands (`pause_music`, `resume_music`, `create_playlist`, `search_songs`).

---

## 3. How to Run Tests Locally

```bash
# Run Core module tests
./gradlew :core:testDebugUnitTest --no-daemon

# Run Playback and Audio DSP tests
./gradlew :playback:testFossDebugUnitTest --no-daemon

# Run Application tests
./gradlew :app:testUniversalFossDebugUnitTest --no-daemon

# Run all test suites simultaneously
./gradlew testUniversalFossDebugUnitTest :core:testDebugUnitTest :playback:testFossDebugUnitTest --no-daemon
```

---

## 4. Current Test Results

| Test Target | Total Tests | Passed | Failed | Success Rate |
| :--- | :--- | :--- | :--- | :--- |
| `:core:testDebugUnitTest` | 11 | 11 | 0 | **100%** |
| `:playback:testFossDebugUnitTest` | 10 | 10 | 0 | **100%** |
| `:app:testUniversalFossDebugUnitTest` | 333 | 333 | 0 | **100%** |
| **Combined Total** | **354** | **354** | **0** | **100% PASS** |
