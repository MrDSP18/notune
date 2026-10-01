package com.music.echo.notune.gesture

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class GestureType {
    SWIPE_LEFT,
    SWIPE_RIGHT,
    SWIPE_UP,
    SWIPE_DOWN,
    DOUBLE_TAP,
    LONG_PRESS,
    SHAPE_CIRCLE,
    SHAPE_HEART,
    SHAPE_X,
    SHAPE_V,
    SHAPE_Z,
    SHAPE_QUESTION,
    SHAPE_STAR,
    SHAPE_SPIRAL,
    SHAPE_LIGHTNING,
    SHAPE_NOTE,
    MACRO_SWIPE_UP_DOUBLE_TAP,
    MACRO_CIRCLE_SWIPE_RIGHT
}

enum class NotuneCommand {
    PLAY_PAUSE,
    NEXT_TRACK,
    PREVIOUS_TRACK,
    OPEN_QUEUE,
    MINIMIZE_PLAYER,
    TOGGLE_FAVORITE,
    OPEN_LYRICS,
    ASK_NOTUNE,
    START_FLOW_MODE,
    TOGGLE_SLEEP_TIMER,
    DISCOVER_MUSIC,
    QUICK_ACTION_MENU,
    MULTI_SELECT,
    NONE
}

data class GestureStateContext(
    val activeContext: GestureContext = GestureContext.GLOBAL,
    val isPlayerExpanded: Boolean = false,
    val isOneHandedActive: Boolean = false
)

@Singleton
class GestureEngine2 @Inject constructor() {
    private val _commandBus = MutableSharedFlow<NotuneCommand>(extraBufferCapacity = 64)
    val commandBus: SharedFlow<NotuneCommand> = _commandBus.asSharedFlow()

    private val customMappings = mutableMapOf<GestureType, NotuneCommand>()
    private var shapeGesturesEnabled = false
    private var macrosEnabled = true

    fun setShapeGesturesEnabled(enabled: Boolean) {
        shapeGesturesEnabled = enabled
    }

    fun setMacrosEnabled(enabled: Boolean) {
        macrosEnabled = enabled
    }

    fun processGesture(gesture: GestureType, stateContext: GestureStateContext) {
        // Resolve custom user override first
        val customCommand = customMappings[gesture]
        if (customCommand != null) {
            _commandBus.tryEmit(customCommand)
            return
        }

        // Standard mapping resolution
        val defaultCommand = resolveDefaultCommand(gesture, stateContext)
        if (defaultCommand != NotuneCommand.NONE) {
            _commandBus.tryEmit(defaultCommand)
        }
    }

    private fun resolveDefaultCommand(gesture: GestureType, stateContext: GestureStateContext): NotuneCommand {
        return when (gesture) {
            GestureType.SWIPE_LEFT -> NotuneCommand.NEXT_TRACK
            GestureType.SWIPE_RIGHT -> NotuneCommand.PREVIOUS_TRACK
            GestureType.SWIPE_UP -> if (stateContext.isPlayerExpanded) NotuneCommand.OPEN_QUEUE else NotuneCommand.NONE
            GestureType.SWIPE_DOWN -> if (stateContext.isPlayerExpanded) NotuneCommand.MINIMIZE_PLAYER else NotuneCommand.NONE
            GestureType.DOUBLE_TAP -> NotuneCommand.TOGGLE_FAVORITE
            GestureType.LONG_PRESS -> NotuneCommand.QUICK_ACTION_MENU

            // Shape gestures (optional layer)
            GestureType.SHAPE_CIRCLE -> if (shapeGesturesEnabled) NotuneCommand.ASK_NOTUNE else NotuneCommand.NONE
            GestureType.SHAPE_HEART -> if (shapeGesturesEnabled) NotuneCommand.TOGGLE_FAVORITE else NotuneCommand.NONE
            GestureType.SHAPE_X -> if (shapeGesturesEnabled) NotuneCommand.NEXT_TRACK else NotuneCommand.NONE
            GestureType.SHAPE_V -> if (shapeGesturesEnabled) NotuneCommand.OPEN_QUEUE else NotuneCommand.NONE
            GestureType.SHAPE_Z -> if (shapeGesturesEnabled) NotuneCommand.TOGGLE_SLEEP_TIMER else NotuneCommand.NONE
            GestureType.SHAPE_QUESTION -> if (shapeGesturesEnabled) NotuneCommand.ASK_NOTUNE else NotuneCommand.NONE
            GestureType.SHAPE_STAR -> if (shapeGesturesEnabled) NotuneCommand.DISCOVER_MUSIC else NotuneCommand.NONE
            GestureType.SHAPE_SPIRAL -> if (shapeGesturesEnabled) NotuneCommand.START_FLOW_MODE else NotuneCommand.NONE
            GestureType.SHAPE_LIGHTNING -> if (shapeGesturesEnabled) NotuneCommand.START_FLOW_MODE else NotuneCommand.NONE
            GestureType.SHAPE_NOTE -> if (shapeGesturesEnabled) NotuneCommand.DISCOVER_MUSIC else NotuneCommand.NONE

            // Macros
            GestureType.MACRO_SWIPE_UP_DOUBLE_TAP -> if (macrosEnabled) NotuneCommand.START_FLOW_MODE else NotuneCommand.NONE
            GestureType.MACRO_CIRCLE_SWIPE_RIGHT -> if (macrosEnabled) NotuneCommand.ASK_NOTUNE else NotuneCommand.NONE
        }
    }

    fun customizeGesture(gesture: GestureType, command: NotuneCommand) {
        customMappings[gesture] = command
    }

    fun resetGestureCustomizations() {
        customMappings.clear()
    }
}
