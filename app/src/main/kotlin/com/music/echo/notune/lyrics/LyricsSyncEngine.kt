package com.music.echo.notune.lyrics

import javax.inject.Inject
import javax.inject.Singleton

data class ActiveLineState(
    val currentLineIndex: Int = -1,
    val currentLine: LyricLine? = null,
    val nextLine: LyricLine? = null,
    val lineProgressPct: Float = 0.0f,
    val activeWordIndex: Int = -1
)

@Singleton
class LyricsSyncEngine @Inject constructor() {

    fun syncWithPosition(currentPositionMs: Long, lines: List<LyricLine>): ActiveLineState {
        if (lines.isEmpty()) return ActiveLineState()

        val activeIndex = lines.indexOfLast { it.startTime <= currentPositionMs }
        if (activeIndex == -1) {
            return ActiveLineState(
                currentLineIndex = 0,
                currentLine = lines.firstOrNull(),
                nextLine = lines.getOrNull(1),
                lineProgressPct = 0f
            )
        }

        val currentLine = lines[activeIndex]
        val nextLine = lines.getOrNull(activeIndex + 1)
        val lineDuration = (currentLine.endTime - currentLine.startTime).coerceAtLeast(100L)
        val elapsed = (currentPositionMs - currentLine.startTime).coerceAtLeast(0L)
        val progressPct = (elapsed.toFloat() / lineDuration.toFloat()).coerceIn(0f, 1f)

        val activeWordIndex = if (currentLine.words.isNotEmpty()) {
            currentLine.words.indexOfLast { it.startTime <= currentPositionMs }
        } else -1

        return ActiveLineState(
            currentLineIndex = activeIndex,
            currentLine = currentLine,
            nextLine = nextLine,
            lineProgressPct = progressPct,
            activeWordIndex = activeWordIndex
        )
    }
}
