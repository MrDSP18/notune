
package echo.music.iad1tya.notune

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import echo.music.iad1tya.R

enum class AppIconType {
    DOT_MATRIX, MINIMAL, NEON, INDUSTRIAL, VINTAGE
}

object BrandingIcons {
    fun getLogoRes(type: AppIconType): Int {
        return when (type) {
            AppIconType.DOT_MATRIX -> R.mipmap.ic_launcher
            AppIconType.MINIMAL -> R.mipmap.ic_launcher_round
            else -> R.mipmap.ic_launcher
        }
    }
}
