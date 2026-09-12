package echo.music.iad1tya.notune.flow

import androidx.media3.common.MediaItem
import echo.music.iad1tya.extensions.toMediaItem
import echo.music.iad1tya.models.MediaMetadata
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlowQueueOptimizer @Inject constructor() {

    fun shouldRefillQueue(
        upcomingItemsCount: Int,
        config: FlowConfig = FlowConfig()
    ): Boolean {
        return upcomingItemsCount < config.minQueueSize
    }

    /**
     * Reconciles current queue items with newly ranked Flow candidates, preserving
     * manual items and locked recommendations.
     */
    fun buildOptimizedQueue(
        existingItems: List<FlowQueueItem>,
        rankedCandidates: List<FlowScore>,
        generationId: Long,
        config: FlowConfig = FlowConfig()
    ): List<FlowQueueItem> {
        val result = mutableListOf<FlowQueueItem>()

        // 1. Retain Manual and Locked items first
        val manualOrLocked = existingItems.filter { it.source != ItemSource.FLOW || it.isLocked }
        result.addAll(manualOrLocked)

        val existingIds = result.map { it.mediaMetadata.id }.toSet()
        val slotsNeeded = (config.targetQueueSize - result.size).coerceAtLeast(0)

        // 2. Fill remaining slots with top ranked candidates
        for (score in rankedCandidates) {
            if (result.size >= config.targetQueueSize) break
            val candidateId = score.candidate.mediaMetadata.id
            if (!existingIds.contains(candidateId)) {
                result.add(
                    FlowQueueItem(
                        mediaMetadata = score.candidate.mediaMetadata,
                        source = ItemSource.FLOW,
                        isLocked = false,
                        reason = score.primaryReason,
                        generationId = generationId
                    )
                )
            }
        }

        return result
    }

    /**
     * Converts FlowQueueItems to Media3 MediaItems for playback injection.
     */
    fun toMediaItems(queueItems: List<FlowQueueItem>): List<MediaItem> {
        return queueItems.map { it.mediaMetadata.toMediaItem() }
    }
}
