package com.music.echo.notune.gesture

/**
 * Composite macro representing a sequence of gesture actions executed sequentially.
 */
data class GestureMacro(
    val id: String,
    val name: String,
    val actions: List<GestureAction>,
    val delayBetweenActionsMs: Long = 300L
)

/**
 * Engine that executes gesture macro sequences.
 */
object GestureMacroEngine {
    private val macros = mutableMapOf<String, GestureMacro>()

    init {
        // Register default built-in macros
        registerMacro(
            GestureMacro(
                id = "macro_ai_queue_boost",
                name = "AI Queue Boost",
                actions = listOf(GestureAction.ToggleQueue, GestureAction.ActivateAiDj)
            )
        )
        registerMacro(
            GestureMacro(
                id = "macro_fav_and_next",
                name = "Favorite and Skip",
                actions = listOf(GestureAction.ToggleFavorite, GestureAction.NextTrack)
            )
        )
    }

    fun registerMacro(macro: GestureMacro) {
        macros[macro.id] = macro
    }

    fun getMacro(macroId: String): GestureMacro? = macros[macroId]

    fun listMacros(): List<GestureMacro> = macros.values.toList()
}
