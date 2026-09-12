
package echo.music.iad1tya.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import echo.music.iad1tya.constants.ArtworkShape
import echo.music.iad1tya.constants.AlbumArtworkShapeKey
import echo.music.iad1tya.utils.rememberEnumPreference
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun rememberArtworkShape(): Shape {
    val artworkShape by rememberEnumPreference(AlbumArtworkShapeKey, ArtworkShape.ROUNDED)
    return when (artworkShape) {
        ArtworkShape.SQUARE -> RectangleShape
        ArtworkShape.ROUNDED -> RoundedCornerShape(12.dp)
        ArtworkShape.CIRCLE -> CircleShape
        ArtworkShape.WAVY -> WavyShape(9, 0.08f, 0f)
        ArtworkShape.NOTHING_INDUSTRIAL -> RoundedCornerShape(2.dp)
    }
}

data class WavyShape(
    val sides: Int,
    val indent: Float,
    val rotationDegrees: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val maxRadiusX = size.width / 2f
        val maxRadiusY = size.height / 2f
        val cx = size.width / 2f
        val cy = size.height / 2f

        val steps = 120
        val rotationRad = rotationDegrees * Math.PI / 180.0
        for (i in 0..steps) {
            val angle = i * Math.PI * 2 / steps
            val bumpAngle = angle - rotationRad
            val r = 1f - indent + indent * cos(sides * bumpAngle)
            val x = cx + maxRadiusX * r * cos(angle)
            val y = cy + maxRadiusY * r * sin(angle)
            if (i == 0) path.moveTo(x.toFloat(), y.toFloat())
            else path.lineTo(x.toFloat(), y.toFloat())
        }
        path.close()
        return Outline.Generic(path)
    }
}
