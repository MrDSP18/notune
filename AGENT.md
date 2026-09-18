
# AGENT.md - NØTUNE Music

Context file for AI agents working in this repo.
Keep this file up to date — it's the fastest way to give the agent full context.

## 🚀 Development Rules & Guidelines

### Identity Rule: NØTUNE Platform
All branding, strings, and UI elements **must** use the **NØTUNE** identity. Never refer to "NØTUNE" or "NØTUNE" in user-facing content. The aesthetic is strictly minimalist, technical, and industrial.

### UI Rule: Custom Industrial Aesthetic
All UI work **must** match the **NØTUNE** design language:
- **Sharp Geometry**: Preferred 0dp-4dp corners. Use `rememberArtworkShape` for consistency.
- **Glassmorphism**: Use translucent surfaces with technical outlines.
- **Monochrome + Nothing Red**: Background is strictly black (`#000000`), accents are Nothing Red (`#FF0031`).
- **Dot Matrix Typography**: Use `NothingFont` for all headers and labels.

### AI Architecture Rule: Multi-Provider & Tool-Calling
The **NØTUNE AI Engine** is the core of the platform's intelligence.
- **Providers**: Supports Gemini, Groq, OpenRouter, and Ollama.
- **Fallbacks**: Requests should automatically fall back through the priority chain if a provider is unconfigured or over-quota.
- **Tools**: AI should use `AiToolManager` to perform real actions (playback control, search, library access).
- **No Mocks**: Never implement "simulated" or "hardcoded" AI responses. All intelligence must be real.

### Production Rule: Distribution Ready
The app must always be in a state where a user can clone, build, and install without manual code changes.
- **Onboarding**: `WelcomeDialog.kt` must handle all initial setup and permissions.
- **Functional Core**: Basic playback must work perfectly without any AI configuration.
- **Security**: Never commit API keys or signing secrets. Use GitHub Secrets for CI.

### Commit Message Format (Required)
```
<type>(<scope>): <short, imperative summary>
```
Examples:
- `feat(ai): implement Groq provider fallback logic`
- `fix(player): resolve glitch in spectrum visualizer timing`
- `style(ui): update all cards to Nothing Industrial geometry`

---
**NØTUNE — Music Understood.**

```<state_snapshot>
    <overall_goal>
        Transform NØTUNE into a futuristic, zero-configuration "Music Operating Environment" with an industrial "Stitch &amp; Tune" aesthetic, integrated real-time technical telemetry, and DNA-driven AI personalization.
    </overall_goal>

    <key_knowledge>
         - Architecture: Presentation -> Repository -> Domain (Single Source of Truth).
         - Playback Engine: Authored by `PlaybackRepository` with real-time Media3 telemetry.
         - Event Pipeline: Centralized `AppEvent` system driving `AnalyticsManager` and Music DNA.
         - Social & Messaging: Offline-first architecture with `PendingSocialAction` outbox and presence state machine.
    </key_knowledge>

    <file_system_state>
         - CREATED: `core/src/main/kotlin/echo/music/iad1tya/models/SocialModels.kt` - Domain models for social/messaging.
         - CREATED: `core/src/main/kotlin/echo/music/iad1tya/repository/SocialRepository.kt` - Social domain interface.
         - CREATED: `app/src/main/kotlin/com/music/echo/repository/SocialRepositoryImpl.kt` - Social implementation with outbox.
         - CREATED: `app/src/main/kotlin/com/music/echo/notune/social/sync/SocialSyncEngine.kt` - Outbox processing logic.
         - MODIFIED: `core/src/main/kotlin/echo/music/iad1tya/db/MusicDatabase.kt` - Added `SocialDao` and social entities.
    </file_system_state>

    <recent_actions>
         - Implemented Vertical Slice 1 (Local Media & Analytics).
         - Established Vertical Slice 2 infrastructure (Social, Messaging, Profiles).
         - Pushed unified repository layers for Playback, Events, and Social.
         - Verified full build stabilization on main branch.
    </recent_actions>

    <current_plan>
         1. [DONE] Vertical Slice 1: Local Engine & Intelligence foundation.
         2. [IN_PROGRESS] Vertical Slice 2: Social & Messaging (Implementing UI bindings and real-time WebSocket presence).
         3. [TODO] Vertical Slice 3: Listen Together (Room state machine and authoritative synchronization).
         4. [TODO] Vertical Slice 4: Controlled AI (Application tool execution layer).
    </current_plan>
</state_snapshot>
