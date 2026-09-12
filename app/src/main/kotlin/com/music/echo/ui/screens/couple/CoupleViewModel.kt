package echo.music.iad1tya.ui.screens.couple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.CoupleSessionEntity
import echo.music.iad1tya.db.entities.MemoryCapsuleEntity
import echo.music.iad1tya.db.entities.OurSongEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

data class CoupleUiState(
    val session: CoupleSessionEntity? = null,
    val primarySong: OurSongEntity? = null,
    val ourSongs: List<OurSongEntity> = emptyList(),
    val capsules: List<MemoryCapsuleEntity> = emptyList(),
    val isVaultUnlocked: Boolean = false,
    val vaultPin: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class CoupleViewModel @Inject constructor(
    private val database: MusicDatabase
) : ViewModel() {

    private val coupleDao = database.coupleDao()

    val activeSession: StateFlow<CoupleSessionEntity?> = coupleDao.activeSession()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isVaultUnlocked = MutableStateFlow(false)
    val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

    val uiState: StateFlow<CoupleUiState> = combine(
        activeSession,
        _isVaultUnlocked
    ) { session, isUnlocked ->
        CoupleUiState(
            session = session,
            isVaultUnlocked = isUnlocked
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CoupleUiState())

    fun createSession(partnerName: String, pairCode: String) {
        viewModelScope.launch {
            val session = CoupleSessionEntity(
                id = UUID.randomUUID().toString(),
                partnerName = partnerName,
                pairCode = pairCode,
                createdAt = LocalDateTime.now(),
                isActive = true
            )
            coupleDao.insertSession(session)
        }
    }

    fun endSession(sessionId: String) {
        viewModelScope.launch {
            coupleDao.deactivateSession(sessionId)
        }
    }

    fun setOurSong(sessionId: String, title: String, artist: String, songId: String, note: String?) {
        viewModelScope.launch {
            coupleDao.clearPrimary(sessionId)
            val ourSong = OurSongEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                songId = songId,
                title = title,
                artist = artist,
                note = note,
                addedAt = LocalDateTime.now(),
                isPrimary = true
            )
            coupleDao.insertOurSong(ourSong)
        }
    }

    fun addMemoryCapsule(sessionId: String, title: String, note: String, songId: String?, unlockAt: LocalDateTime?) {
        viewModelScope.launch {
            val capsule = MemoryCapsuleEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                title = title,
                note = note,
                songId = songId,
                unlockAt = unlockAt,
                createdAt = LocalDateTime.now(),
                isOpened = false
            )
            coupleDao.insertCapsule(capsule)
        }
    }

    fun unlockVault(pin: String): Boolean {
        // Simple demo pin validation (e.g. 1234 or non-empty)
        return if (pin == "1234" || pin.length == 4) {
            _isVaultUnlocked.value = true
            true
        } else {
            false
        }
    }

    fun lockVault() {
        _isVaultUnlocked.value = false
    }
}
