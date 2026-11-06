# NØTUNE Architecture Consolidation Audit

**Date**: October 1, 2026  
**Branch**: `release/notune-final`  
**Commit Target**: Final Production Consolidation Pass  

---

## Executive Summary

As part of the NØTUNE Unified Production Reconstruction pass, all overlapping generations of AI, context, recommendation, flow, and queue engine classes across the codebase have been mapped and consolidated into **ONE authoritative production path**. No competing ranking engines independently score recommendations, and no competing queue managers mutate the playback pipeline.

---

## Component Consolidation Audit Table

| Existing Component | Primary Responsibility | Used By | Replacement / Integration | Final Status |
| :--- | :--- | :--- | :--- | :--- |
| **`NotuneIntelligenceCoordinator`** | Master orchestration of natural language intent, context snapshot, candidate pool search, 5-signal ranking, diversity filtering, natural language queue planning, and Media3 playback trigger. | `AskNoTuneViewModel`, `NotuneIntelligenceEngine` | Primary production coordinator for all music intelligence requests | `PRIMARY` |
| **`NotuneIntelligenceEngine`** | Top-level facade exposing User DNA, Quality Gate evaluation, recommendation generation, non-judgmental state inference, adaptive queue flow, feedback ingestion, and control center summary. | UI Screens (`AskNoTuneScreen`, `NotuneMemoryScreen`, `MusicDnaScreen`), `Media3PlaybackListener` | Primary production intelligence facade | `PRIMARY` |
| **`AiEngine`** | Multi-provider LLM dispatcher (Gemini, Groq, OpenRouter, Ollama, and NoTuneBasicAiProvider). | `AskNoTuneViewModel`, `AiSuiteManager` | Adapter delegating music intent resolution to `NotuneIntelligenceCoordinator` | `ADAPTER` |
| **`NoTuneBasicAiProvider`** | Zero-config offline intelligence provider. | `AiEngine` | Fallback provider when no external LLM API key is configured | `PRIMARY` |
| **`AiToolManager`** | Tool execution manager for LLM tool calls. | `AiEngine` | Adapter forwarding playback, search, and system commands to `AiToolExecutor` & `NotuneToolRegistry` | `ADAPTER` |
| **`AiToolExecutor` & `NotuneToolRegistry`** | Central registry and executor for 29 NØTUNE tools. | `NotuneIntelligenceCoordinator`, `AiToolManager` | Primary production tool execution matrix | `PRIMARY` |
| **`IntentEngine`** | Parses natural language prompts into 12 `StructuredUserIntent` types. | `NotuneIntelligenceCoordinator` | Primary production intent parser | `PRIMARY` |
| **`UnifiedContextEngine`** | Captures 5-layer context snapshot (playback, session, user, environment, room). | `NotuneIntelligenceCoordinator` | Primary production context snapshot engine | `PRIMARY` |
| **`ContextEngine`** | Infers non-invasive situational listening context. | `NotuneIntelligenceEngine` | Integrated component in `NotuneIntelligenceEngine` | `PRIMARY` |
| **`MusicContextEngine`** | Legacy context builder. | Legacy UI callers | Delegated to `UnifiedContextEngine` snapshot | `LEGACY_INTERNAL` |
| **`CandidateEngine` & `CatalogSearchResolver`** | Multi-source candidate generation via `ProviderRegistry`. | `NotuneIntelligenceCoordinator` | Primary candidate generator across local library and YouTube Music | `PRIMARY` |
| **`ProviderRegistry`** | Catalog lookup across `LocalMediaStoreProvider` and `YouTubeInnerTubeProvider`. | `CandidateEngine`, `CatalogSearchResolver` | Primary music provider registry | `PRIMARY` |
| **`CentralizedRankingEngine`** | 5-signal candidate scoring function (TasteMatch, ContextMatch, FlowTransition, Novelty, ProviderQuality). | `NotuneIntelligenceCoordinator` | Primary production candidate ranking engine | `PRIMARY` |
| **`RecommendationEngine`** | Heuristic candidate pool scorer. | `NotuneIntelligenceEngine` | Adapter delegating scoring rules to `CentralizedRankingEngine` | `ADAPTER` |
| **`DiversityController`** | Saturation control & discovery tolerance filtering. | `NotuneIntelligenceCoordinator` | Primary diversity filter | `PRIMARY` |
| **`FlowEngine` / `FlowCandidateScorer` / `FlowQueueOptimizer`** | Legacy FLOW mood radio candidate scorer & queue optimizer. | `NotuneFlowMode` radio selectors | Internal adapters wrapping `CandidateEngine` and `CentralizedRankingEngine` | `LEGACY_INTERNAL` |
| **`AdaptiveQueueOrchestrator`** | 5-zone adaptive queue manager and natural language queue directive executor. | `NaturalLanguageQueueController`, `NotuneIntelligenceCoordinator` | Primary production queue manager; preserves user-locked tracks | `PRIMARY` |
| **`AdaptiveQueueEngine`** | Queue state container (`QueueState`) and flow transition scorer. | `AdaptiveQueueOrchestrator`, `NotuneIntelligenceEngine` | Primary queue state engine | `PRIMARY` |
| **`NaturalLanguageQueueController`** | Executes artist exclusions, language filters, duration limits, and discovery injection. | `NotuneIntelligenceCoordinator` | Primary natural language queue controller | `PRIMARY` |
| **`UserMemoryEngine`** | Manages persistent decaying memory items across EXPLICIT, BEHAVIORAL, SESSION, and MUSIC categories. | `FeedbackEngine`, `CentralizedRankingEngine`, `NotuneMemoryScreen` | Primary production user memory engine | `PRIMARY` |
| **`StructuredTasteModel`** | Dynamic multi-dimensional taste profile representation. | `FeedbackEngine`, `CentralizedRankingEngine` | Primary taste model | `PRIMARY` |
| **`TasteProfileStore`** | Manages `NotuneUserDNA` snapshot and explicit preference rules. | `NotuneIntelligenceEngine`, `AdaptiveQueueEngine` | Primary taste profile store | `PRIMARY` |
| **`TasteProfileRepository`** | DataStore backed taste profile repository. | `NoTuneBasicAiProvider`, `AiEngine` | Adapter bridging legacy UI flows to `TasteProfileStore` | `ADAPTER` |
| **`FeedbackEngine`** | Processes 30 `UserEvent` telemetry types and calculates scalar rewards (-1.0 to +1.0). | `NotuneIntelligenceCoordinator`, `Media3PlaybackListener` | Primary production feedback learning engine | `PRIMARY` |
| **`Media3PlaybackListener`** | ExoPlayer event listener streaming transitions, skips, and completions. | `MusicService` | Primary playback telemetry bridge | `PRIMARY` |
| **`PlayerConnection` & `MusicService`** | Media3 ExoPlayer service and connection manager. | Application-wide playback controls | Primary production playback architecture | `PRIMARY` |

---

## Summary of Consolidation Strategy

1. **Single Production Intent & Ranking Pipeline**: All user prompts and music recommendation queries flow exclusively through `NotuneIntelligenceCoordinator` -> `IntentEngine` -> `CandidateEngine` -> `CentralizedRankingEngine` -> `DiversityController` -> `AdaptiveQueueOrchestrator` -> `PlayerConnection`.
2. **Single Playback Service**: All playback operations execute via `PlayerConnection` and `MusicService` on Media3 ExoPlayer.
3. **Single Telemetry Pipeline**: All playback events stream through `Media3PlaybackListener` -> `UserEvent` -> `FeedbackEngine` -> `StructuredTasteModel` -> `UserMemoryEngine`.
4. **Preserved Compatibility**: Legacy helper classes are retained as internal adapters to prevent breaking existing API contracts or third-party integrations.
