package com.music.echo.notune.gesture

/**
 * Contexts within NØTUNE where gesture actions are evaluated.
 * The same gesture event (e.g. Swipe Left) produces different actions depending on active context.
 */
enum class GestureContext {
    GLOBAL,
    PLAYER,
    MINI_PLAYER,
    QUEUE,
    LYRICS,
    ALBUM_ART,
    PLAYLIST,
    SEARCH,
    AI,
    ROOMS,
    COUPLE,
    PARTY
}
