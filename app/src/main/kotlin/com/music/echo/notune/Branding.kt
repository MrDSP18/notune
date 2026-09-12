
package echo.music.iad1tya.notune

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.CustomAppNameKey
import echo.music.iad1tya.utils.rememberPreference

@Composable
fun rememberAppName(): String {
    val (customName) = rememberPreference(CustomAppNameKey, "")
    return if (customName.isNotBlank()) customName else stringResource(R.string.music)
}
