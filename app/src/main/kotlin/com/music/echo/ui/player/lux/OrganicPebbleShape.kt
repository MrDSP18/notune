package com.music.echo.ui.player.lux

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Organic Pebble Shape & Contour Drawing for luxTune.
 *
 * Faithfully replicates the asymmetrical, fluid pebble silhouette
 * seen in the reference design for album artwork, thumbnails, and
 * outer topographic contours.
 */
class OrganicPebbleShape(
    private val scale: Float = 1f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = createPebblePath(size.width, size.height, scale)
        return Outline.Generic(path)
    }

    companion object {
        /**
         * Builds the signature organic pebble path using cubic Bezier curves.
         * The curve coordinates are normalized to [0, 1] and scaled to [width, height].
         */
        fun createPebblePath(
            width: Float,
            height: Float,
            scale: Float = 1f,
            offsetX: Float = 0f,
            offsetY: Float = 0f
        ): Path {
            val path = Path()
            val cx = width * 0.5f + offsetX
            val cy = height * 0.5f + offsetY

            // Calculate scaled dimensions centered around (cx, cy)
            fun x(normalized: Float): Float = cx + (normalized - 0.5f) * width * scale
            fun y(normalized: Float): Float = cy + (normalized - 0.5f) * height * scale

            // Top apex
            path.moveTo(x(0.48f), y(0.04f))

            // Curve 1: Top across to Top-Right shoulder
            path.cubicTo(
                x(0.72f), y(0.03f),
                x(0.86f), y(0.15f),
                x(0.92f), y(0.34f)
            )

            // Curve 2: Right side downwards into gentle waist
            path.cubicTo(
                x(0.96f), y(0.50f),
                x(0.88f), y(0.66f),
                x(0.84f), y(0.80f)
            )

            // Curve 3: Bottom-Right lobe towards bottom apex
            path.cubicTo(
                x(0.80f), y(0.92f),
                x(0.64f), y(0.97f),
                x(0.48f), y(0.96f)
            )

            // Curve 4: Bottom apex sweeping to Bottom-Left
            path.cubicTo(
                x(0.30f), y(0.95f),
                x(0.14f), y(0.86f),
                x(0.08f), y(0.70f)
            )

            // Curve 5: Left flank curving upward with organic bulge
            path.cubicTo(
                x(0.02f), y(0.52f),
                x(0.06f), y(0.30f),
                x(0.18f), y(0.16f)
            )

            // Curve 6: Top-Left shoulder back to Top apex
            path.cubicTo(
                x(0.28f), y(0.05f),
                x(0.36f), y(0.04f),
                x(0.48f), y(0.04f)
            )

            path.close()
            return path
        }

        /**
         * Renders the outer contour aura around the pebble artwork,
         * the anchor node dot on the left, and subtle topographic background lines.
         */
        fun drawArtworkAuraAndContours(
            drawScope: DrawScope,
            boxWidth: Float,
            boxHeight: Float,
            contourStrokeWidth: Float = 3f,
            auraScale: Float = 1.15f,
            drawBackgroundTopography: Boolean = true
        ) {
            with(drawScope) {
                // 1. Ethereal Topographic background curves
                if (drawBackgroundTopography) {
                    val topo1 = Path().apply {
                        moveTo(-boxWidth * 0.2f, boxHeight * 0.2f)
                        cubicTo(
                            boxWidth * 0.2f, boxHeight * 0.15f,
                            boxWidth * 0.8f, boxHeight * 0.05f,
                            boxWidth * 1.3f, boxHeight * 0.12f
                        )
                    }
                    drawPath(
                        path = topo1,
                        color = LuxDesignTokens.ContourFaint,
                        style = Stroke(width = 1.5f, cap = StrokeCap.Round)
                    )

                    val topo2 = Path().apply {
                        moveTo(-boxWidth * 0.1f, boxHeight * 0.78f)
                        cubicTo(
                            boxWidth * 0.3f, boxHeight * 0.85f,
                            boxWidth * 0.7f, boxHeight * 0.92f,
                            boxWidth * 1.2f, boxHeight * 0.80f
                        )
                    }
                    drawPath(
                        path = topo2,
                        color = LuxDesignTokens.ContourFaint,
                        style = Stroke(width = 1.5f, cap = StrokeCap.Round)
                    )
                }

                // 2. Outer Pebble Aura Contour (scaled ~1.15x)
                val auraPath = createPebblePath(boxWidth, boxHeight, scale = auraScale)
                drawPath(
                    path = auraPath,
                    color = LuxDesignTokens.Contour,
                    style = Stroke(width = contourStrokeWidth, cap = StrokeCap.Round)
                )

                // 3. Horizontal topographic contour reaching outward to the left
                val leftContourPath = Path().apply {
                    val startX = (boxWidth * 0.5f) - (boxWidth * 0.5f * auraScale) + 6f
                    val startY = boxHeight * 0.48f
                    moveTo(startX, startY)
                    cubicTo(
                        startX - boxWidth * 0.12f, startY + 8f,
                        startX - boxWidth * 0.25f, startY - 14f,
                        -boxWidth * 0.35f, startY - 10f
                    )
                }
                drawPath(
                    path = leftContourPath,
                    color = LuxDesignTokens.ContourAccent,
                    style = Stroke(width = contourStrokeWidth, cap = StrokeCap.Round)
                )

                // 4. Solid anchor node dot on the left contour line
                val dotX = (boxWidth * 0.5f) - (boxWidth * 0.5f * auraScale) - 18f
                val dotY = boxHeight * 0.48f + 2f
                drawCircle(
                    color = LuxDesignTokens.Charcoal,
                    radius = 4.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }
        }
    }
}
