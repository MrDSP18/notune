package echo.music.iad1tya.playback

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.di.ApplicationScope
import echo.music.iad1tya.playback.MusicService.MusicBinder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerConnectionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase,
    @ApplicationScope private val scope: CoroutineScope
) {
    private val _playerConnection = MutableStateFlow<PlayerConnection?>(null)
    val playerConnection = _playerConnection.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is MusicBinder) {
                val connection = PlayerConnection(context, service, database, scope)
                _playerConnection.value = connection
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _playerConnection.value?.dispose()
            _playerConnection.value = null
        }
    }

    fun bind() {
        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        context.unbindService(serviceConnection)
        _playerConnection.value?.dispose()
        _playerConnection.value = null
    }
}
