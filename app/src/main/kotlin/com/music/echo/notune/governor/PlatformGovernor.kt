package com.music.echo.notune.governor

import java.util.concurrent.atomic.AtomicReference

enum class GovernorLevel {
    GREEN,   // All features and background prefetches enabled
    YELLOW,  // Throttle background sync, presence ping to 15s
    ORANGE,  // Disable non-essential analytics and lyrics prefetching
    RED      // Emergency mode: protect playback/social core only
}

data class GovernorPolicy(
    val level: GovernorLevel,
    val presenceIntervalMs: Long,
    val lyricsPrefetchEnabled: Boolean,
    val analyticsEnabled: Boolean,
    val maxConcurrentRequests: Int
)

object PlatformGovernor {
    private val currentLevel = AtomicReference(GovernorLevel.GREEN)

    fun setLevel(level: GovernorLevel) {
        currentLevel.set(level)
    }

    fun parseHeader(headerValue: String?): GovernorLevel {
        return when (headerValue?.uppercase()) {
            "YELLOW" -> GovernorLevel.YELLOW
            "ORANGE" -> GovernorLevel.ORANGE
            "RED" -> GovernorLevel.RED
            else -> GovernorLevel.GREEN
        }
    }

    fun getPolicy(): GovernorPolicy {
        return when (currentLevel.get()) {
            GovernorLevel.GREEN -> GovernorPolicy(
                level = GovernorLevel.GREEN,
                presenceIntervalMs = 5_000L,
                lyricsPrefetchEnabled = true,
                analyticsEnabled = true,
                maxConcurrentRequests = 8
            )
            GovernorLevel.YELLOW -> GovernorPolicy(
                level = GovernorLevel.YELLOW,
                presenceIntervalMs = 15_000L,
                lyricsPrefetchEnabled = true,
                analyticsEnabled = true,
                maxConcurrentRequests = 4
            )
            GovernorLevel.ORANGE -> GovernorPolicy(
                level = GovernorLevel.ORANGE,
                presenceIntervalMs = 30_000L,
                lyricsPrefetchEnabled = false,
                analyticsEnabled = false,
                maxConcurrentRequests = 2
            )
            GovernorLevel.RED -> GovernorPolicy(
                level = GovernorLevel.RED,
                presenceIntervalMs = 60_000L,
                lyricsPrefetchEnabled = false,
                analyticsEnabled = false,
                maxConcurrentRequests = 1
            )
        }
    }
}
