package com.music.echo.notune.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.db.DatabaseDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.util.Calendar
import javax.inject.Inject

data class AchievementsUiState(
    val isLoading: Boolean = true,
    val currentLevel: Int = 1,
    val minutesListened: Int = 0,
    val streakDays: Int = 1,
    val levelProgress: Float = 0.0f,
    val badges: List<AchievementBadge> = emptyList()
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementsManager: NotuneAchievementsManager,
    private val databaseDao: DatabaseDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        loadAchievements()
    }

    fun loadAchievements() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val badges = achievementsManager.getAchievements()
            val events = databaseDao.events().first()

            val totalPlays = events.size
            val estimatedMinutes = (totalPlays * 3.5).toInt()
            val level = (estimatedMinutes / 60) + 1
            val progress = ((estimatedMinutes % 60) / 60f).coerceIn(0f, 1f)

            // Streak computation based on distinct days in event history
            val daysSet = events.map { eventWithSong ->
                val timestamp = eventWithSong.event.timestamp.toInstant(ZoneOffset.UTC).toEpochMilli()
                val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
                "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
            }.toSet()

            val streak = if (daysSet.isEmpty()) 1 else daysSet.size

            _uiState.value = AchievementsUiState(
                isLoading = false,
                currentLevel = level,
                minutesListened = estimatedMinutes,
                streakDays = streak,
                levelProgress = progress,
                badges = badges
            )
        }
    }
}
