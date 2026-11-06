package com.music.echo.notune.gesture

/**
 * Settings and feature toggles for the NØTUNE Gesture Engine.
 */
data class GestureSettings(
    val isGestureSystemEnabled: Boolean = true,
    val isShakeToShuffleEnabled: Boolean = true,
    val isGestureEqEnabled: Boolean = true,
    val isDrawnShapesEnabled: Boolean = true,
    val isGlobalThreeFingerEnabled: Boolean = true,
    val isOneHandedModeEnabled: Boolean = false,
    val swipeSensitivity: Float = 1.0f,          // 0.5 (low) to 2.0 (high)
    val strokeConfidenceThreshold: Float = 0.65f, // Min shape match score
    val gestureCooldownMs: Long = 250L,
    val activeContext: GestureContext = GestureContext.PLAYER
)
