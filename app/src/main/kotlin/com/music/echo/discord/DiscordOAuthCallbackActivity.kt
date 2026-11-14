

package echo.music.iad1tya.discord

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import echo.music.iad1tya.BuildConfig

class DiscordOAuthCallbackActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
        finish()
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.scheme.equals(BuildConfig.DISCORD_REDIRECT_SCHEME, ignoreCase = true)) {
            DiscordAuthCoordinator.emit(uri)
        }
    }
}
