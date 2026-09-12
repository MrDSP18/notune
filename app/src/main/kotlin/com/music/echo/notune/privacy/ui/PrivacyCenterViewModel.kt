package com.music.echo.notune.privacy.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.echo.notune.privacy.deletion.DataDeletionManager
import com.music.echo.notune.privacy.export.DataExporter
import com.music.echo.notune.privacy.models.DataInventorySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.*
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PrivacyCenterViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase,
    private val dataExporter: DataExporter,
    private val dataDeletionManager: DataDeletionManager
) : ViewModel() {

    private val _inventorySummary = MutableStateFlow(DataInventorySummary())
    val inventorySummary: StateFlow<DataInventorySummary> = _inventorySummary.asStateFlow()

    private val _exportFile = MutableStateFlow<File?>(null)
    val exportFile: StateFlow<File?> = _exportFile.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    init {
        loadDataInventory()
    }

    fun loadDataInventory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val likedSongsCount = database.likedSongsCount().first()
                val playlistsCount = database.playlists(echo.music.iad1tya.constants.PlaylistSortType.CREATE_DATE, true).first().size
                val historyCount = database.topSongs(1000).first().size

                _inventorySummary.value = DataInventorySummary(
                    listeningHistoryCount = historyCount,
                    favoritesCount = likedSongsCount,
                    playlistsCount = playlistsCount,
                    musicDnaEnabled = true,
                    flowEnabled = true,
                    cloudSyncEnabled = false,
                    analyticsEnabled = false
                )
            } catch (e: Exception) {
                Timber.e(e, "PRIVACY_CENTER: Failed to load inventory")
            }
        }
    }

    fun triggerDataExport() {
        viewModelScope.launch(Dispatchers.IO) {
            _isExporting.value = true
            try {
                val file = dataExporter.generateDataExport()
                _exportFile.value = file
                Timber.d("PRIVACY_CENTER: Export completed -> ${file.absolutePath}")
            } catch (e: Exception) {
                Timber.e(e, "PRIVACY_CENTER: Export failed")
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun clearListeningHistory() {
        viewModelScope.launch {
            dataDeletionManager.clearListeningHistory()
            loadDataInventory()
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            dataDeletionManager.clearSearchHistory()
            loadDataInventory()
        }
    }

    fun clearAiData() {
        viewModelScope.launch {
            dataDeletionManager.clearAiData()
            loadDataInventory()
        }
    }

    fun fullDataReset() {
        viewModelScope.launch {
            dataDeletionManager.fullAccountAndDataReset()
            loadDataInventory()
        }
    }
}
