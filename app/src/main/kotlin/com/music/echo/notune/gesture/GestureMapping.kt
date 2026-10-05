package com.music.echo.notune.gesture

/**
 * Context-aware mapping engine that resolves (GestureEvent, GestureContext) -> GestureAction.
 */
object GestureMapping {

    /**
     * Resolves the default or custom [GestureAction] for a given [event] in a given [context].
     */
    fun resolveAction(event: GestureEvent, context: GestureContext): GestureAction {
        // Handle drawn shape gestures universally across contexts if AI/Shape enabled
        if (event is GestureEvent.DrawnShape) {
            return resolveDrawnShapeAction(event.shape)
        }

        // Handle Shake universally
        if (event is GestureEvent.ShakeDevice) {
            return when (context) {
                GestureContext.PARTY -> GestureAction.PartyBoost
                else -> GestureAction.ShuffleQueue
            }
        }

        // Handle TriggerMacro
        if (event is GestureEvent.TriggerMacro) {
            return GestureAction.ExecuteMacro(event.macroId)
        }

        // Handle Voice + Gesture combination
        if (event is GestureEvent.VoiceWithGesture) {
            return GestureAction.AskNotune(event.voiceCommand)
        }

        return when (context) {
            GestureContext.PLAYER -> resolvePlayerAction(event)
            GestureContext.MINI_PLAYER -> resolveMiniPlayerAction(event)
            GestureContext.QUEUE -> resolveQueueAction(event)
            GestureContext.LYRICS -> resolveLyricsAction(event)
            GestureContext.ALBUM_ART -> resolveAlbumArtAction(event)
            GestureContext.SEARCH -> resolveSearchAction(event)
            GestureContext.ROOMS -> resolveRoomsAction(event)
            GestureContext.COUPLE -> resolveCoupleAction(event)
            GestureContext.PARTY -> resolvePartyAction(event)
            GestureContext.GLOBAL -> resolveGlobalAction(event)
            else -> resolveGlobalAction(event)
        }
    }

    private fun resolveDrawnShapeAction(shape: GestureEvent.ShapeType): GestureAction {
        return when (shape) {
            GestureEvent.ShapeType.CIRCLE -> GestureAction.OpenAi
            GestureEvent.ShapeType.HEART -> GestureAction.ToggleFavorite
            GestureEvent.ShapeType.CROSS_X -> GestureAction.NextTrack
            GestureEvent.ShapeType.V_SHAPE -> GestureAction.PlayNext
            GestureEvent.ShapeType.Z_SHAPE -> GestureAction.SleepTimer
            GestureEvent.ShapeType.QUESTION_MARK -> GestureAction.AskNotune("Help me with NØTUNE")
            GestureEvent.ShapeType.LIGHTNING -> GestureAction.ActivateAiDj
            GestureEvent.ShapeType.MUSIC_NOTE -> GestureAction.AiDiscovery
            GestureEvent.ShapeType.SPIRAL -> GestureAction.AiFlow
            GestureEvent.ShapeType.STAR -> GestureAction.AiFindGems
        }
    }

    private fun resolvePlayerAction(event: GestureEvent): GestureAction {
        return when (event) {
            is GestureEvent.SingleTap -> GestureAction.TogglePlayPause
            is GestureEvent.DoubleTap -> GestureAction.ToggleFavorite
            is GestureEvent.TripleTap -> GestureAction.FavoriteWithAnimation
            is GestureEvent.LongPress -> GestureAction.OpenTrackActions

            is GestureEvent.Swipe -> when {
                event.fingers == 2 && event.direction == GestureEvent.SwipeDirection.UP -> GestureAction.VolumeUp
                event.fingers == 2 && event.direction == GestureEvent.SwipeDirection.DOWN -> GestureAction.VolumeDown
                event.fingers == 2 && event.direction == GestureEvent.SwipeDirection.LEFT -> GestureAction.PreviousTrack
                event.fingers == 2 && event.direction == GestureEvent.SwipeDirection.RIGHT -> GestureAction.NextTrack
                event.direction == GestureEvent.SwipeDirection.LEFT -> GestureAction.NextTrack
                event.direction == GestureEvent.SwipeDirection.RIGHT -> GestureAction.PreviousTrack
                event.direction == GestureEvent.SwipeDirection.UP -> GestureAction.ToggleQueue
                event.direction == GestureEvent.SwipeDirection.DOWN -> GestureAction.MinimizePlayer
                else -> GestureAction.None
            }

            is GestureEvent.Pinch -> if (event.isPinchIn) GestureAction.CompactPlayer else GestureAction.FullPlayer
            is GestureEvent.TwoFingerRotate -> if (event.isClockwise) GestureAction.ChangeSpeed(0.25f) else GestureAction.ChangeSpeed(-0.25f)

            is GestureEvent.EqPinch -> if (event.delta > 0) GestureAction.AdjustBass(1.5f) else GestureAction.AdjustBass(-1.5f)
            is GestureEvent.EqHorizontal -> if (event.delta > 0) GestureAction.AdjustTreble(1.5f) else GestureAction.AdjustTreble(-1.5f)
            is GestureEvent.EqVertical -> GestureAction.AdjustEqIntensity(event.delta)

            else -> GestureAction.None
        }
    }

    private fun resolveMiniPlayerAction(event: GestureEvent): GestureAction {
        return when (event) {
            is GestureEvent.SingleTap -> GestureAction.TogglePlayPause
            is GestureEvent.DoubleTap -> GestureAction.ToggleFavorite
            is GestureEvent.LongPress -> GestureAction.OpenTrackActions
            is GestureEvent.Swipe -> when (event.direction) {
                GestureEvent.SwipeDirection.LEFT -> GestureAction.PreviousTrack
                GestureEvent.SwipeDirection.RIGHT -> GestureAction.NextTrack
                GestureEvent.SwipeDirection.UP -> GestureAction.ExpandPlayer
                GestureEvent.SwipeDirection.DOWN -> GestureAction.MinimizePlayer
            }
            else -> GestureAction.None
        }
    }

    private fun resolveQueueAction(event: GestureEvent): GestureAction {
        return when (event) {
            is GestureEvent.Swipe -> when {
                event.isLong && event.direction == GestureEvent.SwipeDirection.RIGHT -> GestureAction.MoveToQueueTop
                event.isLong && event.direction == GestureEvent.SwipeDirection.LEFT -> GestureAction.MoveToQueueBottom
                event.direction == GestureEvent.SwipeDirection.RIGHT -> GestureAction.PlayNext
                event.direction == GestureEvent.SwipeDirection.LEFT -> GestureAction.RemoveFromQueue
                else -> GestureAction.None
            }
            else -> GestureAction.None
        }
    }

    private fun resolveLyricsAction(event: GestureEvent): GestureAction {
        return when (event) {
            is GestureEvent.DoubleTap -> GestureAction.LyricsKaraoke
            is GestureEvent.LongPress -> GestureAction.LyricsTranslate
            is GestureEvent.ThreeFingerTap -> GestureAction.TogglePlayPause
            is GestureEvent.Swipe -> when {
                event.fingers == 3 && event.direction == GestureEvent.SwipeDirection.LEFT -> GestureAction.PreviousTrack
                event.fingers == 3 && event.direction == GestureEvent.SwipeDirection.RIGHT -> GestureAction.NextTrack
                event.direction == GestureEvent.SwipeDirection.UP -> GestureAction.NextTrack
                event.direction == GestureEvent.SwipeDirection.DOWN -> GestureAction.PreviousTrack
                else -> GestureAction.None
            }
            else -> GestureAction.None
        }
    }

    private fun resolveAlbumArtAction(event: GestureEvent): GestureAction {
        return resolvePlayerAction(event)
    }

    private fun resolveSearchAction(event: GestureEvent): GestureAction {
        return when (event) {
            is GestureEvent.SingleTap -> GestureAction.None
            is GestureEvent.DoubleTap -> GestureAction.TogglePlayPause
            is GestureEvent.LongPress -> GestureAction.AskNotune("Find songs like this but less mainstream")
            is GestureEvent.Swipe -> when (event.direction) {
                GestureEvent.SwipeDirection.DOWN -> GestureAction.OpenSearch
                GestureEvent.SwipeDirection.RIGHT -> GestureAction.PlayNext
                else -> GestureAction.None
            }
            else -> GestureAction.None
        }
    }

    private fun resolveRoomsAction(event: GestureEvent): GestureAction {
        return when (event) {
            is GestureEvent.DoubleTap -> GestureAction.RoomVoteUp
            is GestureEvent.TwoFingerTap -> GestureAction.ToggleRoomSync
            is GestureEvent.Swipe -> when (event.direction) {
                GestureEvent.SwipeDirection.UP -> GestureAction.ToggleQueue
                GestureEvent.SwipeDirection.RIGHT -> GestureAction.RoomVoteUp
                GestureEvent.SwipeDirection.LEFT -> GestureAction.RoomVoteDown
                else -> GestureAction.None
            }
            else -> GestureAction.None
        }
    }

    private fun resolveCoupleAction(event: GestureEvent): GestureAction {
        return when (event) {
            is GestureEvent.DoubleTap -> GestureAction.CoupleAddSharedPlaylist
            is GestureEvent.LongPress -> GestureAction.CoupleAddSharedPlaylist
            is GestureEvent.Swipe -> if (event.direction == GestureEvent.SwipeDirection.RIGHT) GestureAction.CoupleSendSong else GestureAction.None
            else -> GestureAction.None
        }
    }

    private fun resolvePartyAction(event: GestureEvent): GestureAction {
        return when (event) {
            is GestureEvent.DoubleTap -> GestureAction.RoomVoteUp
            is GestureEvent.TwoFingerTap -> GestureAction.PartyBoost
            else -> GestureAction.None
        }
    }

    private fun resolveGlobalAction(event: GestureEvent): GestureAction {
        return when (event) {
            is GestureEvent.ThreeFingerTap -> GestureAction.TogglePlayPause
            is GestureEvent.Swipe -> when {
                event.fingers == 3 && event.direction == GestureEvent.SwipeDirection.DOWN -> GestureAction.OpenSettings
                event.fingers == 3 && event.direction == GestureEvent.SwipeDirection.UP -> GestureAction.OpenAi
                event.fingers == 3 && event.direction == GestureEvent.SwipeDirection.LEFT -> GestureAction.PreviousTrack
                event.fingers == 3 && event.direction == GestureEvent.SwipeDirection.RIGHT -> GestureAction.NextTrack
                else -> GestureAction.None
            }
            else -> GestureAction.None
        }
    }
}
