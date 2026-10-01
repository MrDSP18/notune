package com.music.echo.notune.gesture

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import javax.inject.Inject
import javax.inject.Singleton

enum class OneHandedMode {
    DISABLED,
    LEFT_HAND,
    RIGHT_HAND,
    AUTO
}

@Singleton
class OneHandedManager @Inject constructor() {
    var mode: OneHandedMode = OneHandedMode.DISABLED

    fun getControlAlignment(defaultAlignment: Alignment): Alignment {
        return when (mode) {
            OneHandedMode.LEFT_HAND -> Alignment.BottomStart
            OneHandedMode.RIGHT_HAND -> Alignment.BottomEnd
            OneHandedMode.AUTO -> defaultAlignment
            OneHandedMode.DISABLED -> defaultAlignment
        }
    }

    fun getHorizontalOffset(): Dp {
        return when (mode) {
            OneHandedMode.LEFT_HAND -> (-16).dp
            OneHandedMode.RIGHT_HAND -> 16.dp
            else -> 0.dp
        }
    }
}
