package echo.music.iad1tya.notune.flow

import android.content.Context
import androidx.annotation.VisibleForTesting
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages 24-hour auto-queue cooldown windows.
 *
 * Guarantees that any song automatically added to the queue by the app (Flow, Auto-Load,
 * Radio, or Endless Queue) is placed on a strict 24-hour cooldown timer. During this 24-hour
 * window, the song CANNOT be automatically re-added to the queue. This prevents cyclic
 * repetitions, breaks infinite radio loops, and compels continuous musical exploration.
 *
 * Manual user actions (user explicitly tapping play or add-to-queue) are never blocked.
 */
@Singleton
class AutoQueueCooldownManager @Inject constructor(
    private val context: Context?
) {
    companion object {
        const val TAG = "AutoQueueCooldown"
        const val COOLDOWN_DURATION_MS = 24 * 60 * 60 * 1000L // 24 Hours
        private const val STORAGE_FILE_NAME = "auto_queue_24h_cooldown.json"
        
        @Volatile
        private var instance: AutoQueueCooldownManager? = null

        fun getInstance(context: Context? = null): AutoQueueCooldownManager {
            return instance ?: synchronized(this) {
                instance ?: AutoQueueCooldownManager(context?.applicationContext).also {
                    it.loadFromDisk()
                    instance = it
                }
            }
        }
    }

    private val autoAddedTimestamps = ConcurrentHashMap<String, Long>()

    init {
        loadFromDisk()
    }

    /**
     * Checks if a track is eligible for automatic addition to the playback queue.
     *
     * @return true if the track was never auto-added or its last auto-addition was >= 24 hours ago.
     *         false if the track was auto-added within the last 24 hours (cooling down).
     */
    fun isEligibleForAutoAdd(trackId: String, currentTimeMs: Long = System.currentTimeMillis()): Boolean {
        if (trackId.isBlank()) return false
        val lastAdded = autoAddedTimestamps[trackId] ?: return true
        val elapsed = currentTimeMs - lastAdded
        return elapsed >= COOLDOWN_DURATION_MS
    }

    /**
     * Gets the remaining cooldown time in milliseconds for a given track.
     * Returns 0L if eligible, or remaining millis if cooling down.
     */
    fun getRemainingCooldownMs(trackId: String, currentTimeMs: Long = System.currentTimeMillis()): Long {
        val lastAdded = autoAddedTimestamps[trackId] ?: return 0L
        val elapsed = currentTimeMs - lastAdded
        val remaining = COOLDOWN_DURATION_MS - elapsed
        return if (remaining > 0L) remaining else 0L
    }

    /**
     * Records that a track was automatically added to the queue, initiating its 24-hour cooldown timer.
     */
    fun recordAutoAdded(trackId: String, timestampMs: Long = System.currentTimeMillis()) {
        if (trackId.isBlank()) return
        autoAddedTimestamps[trackId] = timestampMs
        Timber.tag(TAG).d("Engaged 24-hour auto-queue cooldown for track: $trackId")
        saveToDisk()
    }

    /**
     * Bulk records auto-added tracks.
     */
    fun recordAutoAdded(trackIds: Collection<String>, timestampMs: Long = System.currentTimeMillis()) {
        if (trackIds.isEmpty()) return
        for (id in trackIds) {
            if (id.isNotBlank()) {
                autoAddedTimestamps[id] = timestampMs
            }
        }
        Timber.tag(TAG).d("Engaged 24-hour cooldown for ${trackIds.size} auto-added tracks")
        saveToDisk()
    }

    /**
     * Prunes entries older than 24 hours to keep memory and storage footprint minimal.
     */
    fun pruneExpired(currentTimeMs: Long = System.currentTimeMillis()) {
        val iterator = autoAddedTimestamps.entries.iterator()
        var removedCount = 0
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (currentTimeMs - entry.value >= COOLDOWN_DURATION_MS) {
                iterator.remove()
                removedCount++
            }
        }
        if (removedCount > 0) {
            Timber.tag(TAG).d("Pruned $removedCount expired tracks from 24-hour cooldown registry")
            saveToDisk()
        }
    }

    /**
     * Returns a set of all track IDs currently under active 24-hour cooldown.
     */
    fun getActiveCooldownTrackIds(currentTimeMs: Long = System.currentTimeMillis()): Set<String> {
        return autoAddedTimestamps.filterValues { currentTimeMs - it < COOLDOWN_DURATION_MS }.keys.toSet()
    }

    @VisibleForTesting
    fun clearAll() {
        autoAddedTimestamps.clear()
        saveToDisk()
    }

    private fun getStorageFile(): File? {
        val ctx = context ?: return null
        return File(ctx.filesDir, STORAGE_FILE_NAME)
    }

    @Synchronized
    private fun saveToDisk() {
        try {
            val file = getStorageFile() ?: return
            val json = JSONObject()
            val now = System.currentTimeMillis()
            for ((key, value) in autoAddedTimestamps) {
                if (now - value < COOLDOWN_DURATION_MS) {
                    json.put(key, value)
                }
            }
            file.writeText(json.toString())
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Failed to persist 24h auto-queue cooldown state to disk")
        }
    }

    @Synchronized
    private fun loadFromDisk() {
        try {
            val file = getStorageFile() ?: return
            if (!file.exists()) return
            val content = file.readText()
            if (content.isBlank()) return
            val json = JSONObject(content)
            val now = System.currentTimeMillis()
            autoAddedTimestamps.clear()
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val timestamp = json.getLong(key)
                if (now - timestamp < COOLDOWN_DURATION_MS) {
                    autoAddedTimestamps[key] = timestamp
                }
            }
            Timber.tag(TAG).d("Loaded ${autoAddedTimestamps.size} tracks into 24h auto-queue cooldown registry")
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Failed to load 24h auto-queue cooldown state from disk")
        }
    }
}
