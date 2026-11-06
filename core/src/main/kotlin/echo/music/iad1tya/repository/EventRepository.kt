package echo.music.iad1tya.repository

import echo.music.iad1tya.models.AppEvent
import kotlinx.coroutines.flow.SharedFlow

interface EventRepository {
    val events: SharedFlow<AppEvent>
    fun emit(event: AppEvent)
}
