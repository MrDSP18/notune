# NØTUNE AI Architecture & Zero-Config Router

## 1. Zero-Configuration Principle
NØTUNE is designed so normal users are **never required** to configure technical AI parameters, provider URLs, or API keys.

By default, NØTUNE operates using **Local NØTUNE Intelligence** (`NoTuneBasicAiProvider`), providing immediate deterministic query parsing, audio feature analysis, and recommendation generation offline.

---

## 2. Layered AI Fallback Router

```
                   User Prompt / Intent Query
                               │
                               ▼
                    NØTUNE AI Router Engine
                               │
            ┌──────────────────┼──────────────────┐
            ▼                  ▼                  ▼
       Cloud AI             Local AI     NØTUNE Intelligence
     (Gemini/Groq)        (Ollama/etc.)     (Deterministic Rule Engine)
     [If Configured]      [If Available]   [Always Available Fallback]
            │                  │                  │
            └──────────────────┼──────────────────┘
                               ▼
                       Structured Intent
                               │
                               ▼
                    NØTUNE FLOW Queue / Player
```

---

## 3. Structured Intent Control
LLM text outputs are never allowed to execute uncontrolled commands on the player. All AI interactions pass through structured intent normalization (`ConversationalPlaybackEngine` & `MultilingualIntent`):

- **Structured Intent**: Output contains typed parameters (`action`, `targetEnergy`, `extractedMood`, `extractedEra`, `languages`).
- **Media Control**: Intent is validated by `FlowEngine` before mutating Media3 player state or queue.
- **Privacy Boundary**: `PrivacyFilter` strips personal user identifiers before dispatching requests to external API providers.
