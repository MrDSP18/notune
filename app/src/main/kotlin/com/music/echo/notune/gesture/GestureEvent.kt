package com.music.echo.notune.gesture

/**
 * Recognized gesture events processed by the [GestureEngine].
 */
sealed class GestureEvent {
    // Basic Touch Gestures
    object SingleTap : GestureEvent()
    object DoubleTap : GestureEvent()
    object TripleTap : GestureEvent()
    object LongPress : GestureEvent()

    // Directional Swipes
    enum class SwipeDirection { LEFT, RIGHT, UP, DOWN }
    data class Swipe(val direction: SwipeDirection, val fingers: Int = 1, val isLong: Boolean = false) : GestureEvent()

    // Multi-Finger Touch
    object TwoFingerTap : GestureEvent()
    object ThreeFingerTap : GestureEvent()

    // Scale / Pinch / Rotate
    data class Pinch(val scaleFactor: Float) : GestureEvent() {
        val isPinchIn: Boolean get() = scaleFactor < 1.0f
        val isPinchOut: Boolean get() = scaleFactor > 1.0f
    }
    data class TwoFingerRotate(val degrees: Float) : GestureEvent() {
        val isClockwise: Boolean get() = degrees > 0
    }

    // Drawn Symbols & Shapes
    enum class ShapeType {
        CIRCLE,          // ⭕ -> AI Open / Prompt
        HEART,           // ♡ -> Like / Favorite
        CROSS_X,         // ❌ -> Skip Track
        V_SHAPE,         // V -> Add to Queue
        Z_SHAPE,         // Z -> Sleep Timer
        QUESTION_MARK,   // ? -> Ask NØTUNE
        LIGHTNING,       // ⚡ -> AI DJ
        MUSIC_NOTE,      // ♪ -> AI Discovery
        SPIRAL,          // 🌀 -> AI Flow
        STAR             // ☆ -> AI Gems
    }
    data class DrawnShape(val shape: ShapeType, val confidence: Float) : GestureEvent()

    // Gesture EQ Adjustments
    data class EqPinch(val delta: Float) : GestureEvent()
    data class EqHorizontal(val delta: Float) : GestureEvent()
    data class EqVertical(val delta: Float) : GestureEvent()

    // Motion & Multimodal
    object ShakeDevice : GestureEvent()
    data class TriggerMacro(val macroId: String) : GestureEvent()
    data class VoiceWithGesture(val gesture: GestureEvent, val voiceCommand: String) : GestureEvent()
}
