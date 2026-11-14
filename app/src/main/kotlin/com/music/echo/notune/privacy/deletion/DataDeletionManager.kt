package com.music.echo.notune.privacy.deletion

import android.content.Context
import com.music.echo.notune.sync.CloudSyncManager
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.notune.ai.engine.NoMindMemory
import echo.music.iad1tya.utils.dataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataDeletionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase,
    private val mindMemory: NoMindMemory,
    private val cloudSyncManager: CloudSyncManager
) {

    suspend fun clearListeningHistory() = withContext(Dispatchers.IO) {
        try {
            database.query {
                clearListenHistory()
            }
            Timber.d("DATA_DELETION: Listening history cleared.")
        } catch (e: Exception) {
            Timber.e(e, "DATA_DELETION: Failed to clear listening history.")
        }
    }

    suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        try {
            database.query {
                clearSearchHistory()
            }
            Timber.d("DATA_DELETION: Search history cleared.")
        } catch (e: Exception) {
            Timber.e(e, "DATA_DELETION: Failed to clear search history.")
        }
    }

    suspend fun clearAiData() = withContext(Dispatchers.IO) {
        try {
            val aiCacheDir = File(context.cacheDir, "ai_cache")
            if (aiCacheDir.exists()) {
                aiCacheDir.deleteRecursively()
            }
            mindMemory.clearMemory()
            Timber.d("DATA_DELETION: AI cached data and learned memory facts cleared.")
        } catch (e: Exception) {
            Timber.e(e, "DATA_DELETION: Failed to clear AI data.")
        }
    }

    suspend fun fullAccountAndDataReset() = withContext(Dispatchers.IO) {
        try {
            clearListeningHistory()
            clearSearchHistory()
            clearAiData()
            cloudSyncManager.clearPendingOutbox()
            database.clearAllData()
            context.dataStore.edit { it.clear() }
            clearDirectoryChildren(context.cacheDir)
            clearDirectoryChildren(context.filesDir)
            runCatching { context.noBackupFilesDir }.getOrNull()?.let { clearDirectoryChildren(it) }

            // Delete exported files
            val exportDir = File(context.cacheDir, "exports")
            if (exportDir.exists()) {
                exportDir.deleteRecursively()
            }

            Timber.d("DATA_DELETION: Full account and data reset executed successfully.")
        } catch (e: Exception) {
            Timber.e(e, "DATA_DELETION: Error during full data reset.")
        }
    }

    private fun clearDirectoryChildren(directory: File) {
        directory.listFiles()?.forEach { child ->
            if (!child.deleteRecursively() && child.exists()) {
                throw IllegalStateException("Failed to delete local data: ${child.name}")
            }
        }
    }
}
