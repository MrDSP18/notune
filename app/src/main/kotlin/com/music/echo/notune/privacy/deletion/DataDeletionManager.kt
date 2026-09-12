package com.music.echo.notune.privacy.deletion

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.db.MusicDatabase
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
    private val database: MusicDatabase
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
            Timber.d("DATA_DELETION: AI cached data cleared.")
        } catch (e: Exception) {
            Timber.e(e, "DATA_DELETION: Failed to clear AI data.")
        }
    }

    suspend fun fullAccountAndDataReset() = withContext(Dispatchers.IO) {
        try {
            clearListeningHistory()
            clearSearchHistory()
            clearAiData()
            database.clearAllData()
            context.dataStore.edit { it.clear() }

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
}
