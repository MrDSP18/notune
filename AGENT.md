
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
