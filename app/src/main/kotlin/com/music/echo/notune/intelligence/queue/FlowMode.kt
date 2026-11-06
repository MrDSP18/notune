package com.music.echo.notune.intelligence.queue

/**
 * Single simple user-facing control for NØTUNE recommendation flow.
 */
enum class NotuneFlowMode(val label: String, val description: String) {
    FAMILIAR("Familiar", "Focus on songs you love and know well"),
    BALANCED("Balanced", "A comfortable mix of favorites and gentle finds"),
    SMART("Smart", "Default adaptive AI flow based on your session"),
    DISCOVERY("Discovery", "Introduce new artists matching your taste"),
    DEEP_DISCOVERY("Deep Discovery", "Explore hidden gems and fresh musical directions")
}
