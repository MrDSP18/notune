package com.music.echo.notune.social.sync

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.PendingSocialActionEntity
import echo.music.iad1tya.utils.NetworkConnectivityObserver
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class SocialSyncEngine @Inject constructor(
    private val database: MusicDatabase,
    private val httpClient: HttpClient,
    private val networkObserver: NetworkConnectivityObserver,
    @echo.music.iad1tya.di.ApplicationScope private val scope: CoroutineScope
) {
    init {
        scope.launch {
            networkObserver.networkStatus.collectLatest { isOnline ->
                if (isOnline) {
                    processOutbox()
                }
            }
        }
    }

    private suspend fun processOutbox() {
        database.socialDao.getPendingActions().collectLatest { actions ->
            for (action in actions) {
                val result = performRemoteAction(action)
                if (result.isSuccess) {
                    database.socialDao.deletePendingAction(action)
                } else {
                    Timber.tag("SocialSync").e("Failed to sync action: ${action.actionType}")
                    break
                }
            }
        }
    }

    private suspend fun performRemoteAction(action: PendingSocialActionEntity): Result<Unit> = runCatching {
        throw UnsupportedOperationException(
            "Social backend transport is not configured; pending actions must remain in the outbox."
        )
    }
}
