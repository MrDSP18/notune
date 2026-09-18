package com.music.echo.repository

import echo.music.iad1tya.models.AppEvent
import echo.music.iad1tya.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    @echo.music.iad1tya.di.ApplicationScope private val scope: CoroutineScope
) : EventRepository {
    private val _events = MutableSharedFlow<AppEvent>()
    override val events = _events.asSharedFlow()

    override fun emit(event: AppEvent) {
        scope.launch {
            _events.emit(event)
        }
    }
}
