package echo.music.iad1tya.notune.ai.adaptive

/**
 * Model source identifiers for recommendations across platform runtimes.
 */
enum class ModelSource {
    /** Native Android LiteRT-LM (Gemma / Qwen) on-device runtime. */
    ANDROID_LITERT,

    /** Web platform acoustic vector matrix engine (cosine similarity). */
    WEB_VECTOR,

    /** Cloudflare Edge worker D1 SQL similarity engine. */
    EDGE_RANKER,

    /** Deterministic rule-based fallback recommendation engine. */
    FALLBACK
}
