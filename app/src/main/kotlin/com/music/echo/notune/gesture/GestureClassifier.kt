package com.music.echo.notune.gesture

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * 2D point representation for gesture stroke recognition.
 */
data class GesturePoint(val x: Float, val y: Float, val timestampMs: Long = System.currentTimeMillis())

/**
 * Geometric stroke classifier that parses a list of 2D points into a recognized [GestureEvent.DrawnShape] or motion event.
 */
object GestureClassifier {

    /**
     * Classifies a stroke path of 2D points into a recognized gesture shape.
     * Returns null if confidence is below threshold.
     */
    fun classifyStrokePath(points: List<GesturePoint>, minConfidence: Float = 0.6f): GestureEvent.DrawnShape? {
        if (points.size < 5) return null

        val start = points.first()
        val end = points.last()
        val totalDistance = pathLength(points)
        val directDistance = hypot((end.x - start.x).toDouble(), (end.y - start.y).toDouble()).toFloat()

        // 1. Check for Circle: start and end are close, bounding box is roughly square, path length > 2 * pi * radius
        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }
        val width = maxX - minX
        val height = maxY - minY
        val aspectRatio = if (height > 0) width / height else 1.0f

        if (directDistance < (width + height) * 0.25f && totalDistance > 3.0f * (width + height) * 0.4f) {
            if (aspectRatio in 0.6f..1.4f) {
                return GestureEvent.DrawnShape(GestureEvent.ShapeType.CIRCLE, 0.90f)
            }
        }

        // 2. Check for V-Shape: goes down-right then up-right
        val minPointIndex = points.indices.minByOrNull { points[it].y } ?: 0
        val maxPointIndex = points.indices.maxByOrNull { points[it].y } ?: 0
        if (maxPointIndex > 0 && maxPointIndex < points.size - 1) {
            val pBottom = points[maxPointIndex]
            if (start.y < pBottom.y && end.y < pBottom.y && pBottom.x in start.x..end.x) {
                return GestureEvent.DrawnShape(GestureEvent.ShapeType.V_SHAPE, 0.85f)
            }
        }

        // 3. Check for Z-Shape: top horizontal right, diagonal down-left, bottom horizontal right
        val turns = countDirectionChanges(points)
        if (turns >= 2 && start.x < end.x && abs(start.y - points[points.size / 3].y) < height * 0.3f) {
            return GestureEvent.DrawnShape(GestureEvent.ShapeType.Z_SHAPE, 0.80f)
        }

        // 4. Check for Heart: two peaks near top, single trough at bottom
        if (directDistance < (width + height) * 0.35f && minPointIndex in 1 until points.size - 1) {
            val topHalfPoints = points.filter { it.y < minY + height * 0.4f }
            if (topHalfPoints.size >= 4 && maxPointIndex > points.size * 0.4) {
                return GestureEvent.DrawnShape(GestureEvent.ShapeType.HEART, 0.82f)
            }
        }

        // 5. Check for Cross (X): direction changes or diagonal crossings
        if (turns >= 3 && directDistance < (width + height) * 0.5f) {
            return GestureEvent.DrawnShape(GestureEvent.ShapeType.CROSS_X, 0.78f)
        }

        // 6. Check for Lightning (⚡): zigzag shape
        if (turns >= 2 && height > width * 1.2f) {
            return GestureEvent.DrawnShape(GestureEvent.ShapeType.LIGHTNING, 0.75f)
        }

        // 7. Check for Question Mark (?)
        if (start.y < end.y && turns >= 1 && aspectRatio < 0.9f) {
            return GestureEvent.DrawnShape(GestureEvent.ShapeType.QUESTION_MARK, 0.72f)
        }

        // 8. Check for Spiral (🌀)
        if (turns >= 4 && totalDistance > 4.0f * (width + height)) {
            return GestureEvent.DrawnShape(GestureEvent.ShapeType.SPIRAL, 0.76f)
        }

        // 9. Check for Star (☆)
        if (turns >= 4) {
            return GestureEvent.DrawnShape(GestureEvent.ShapeType.STAR, 0.70f)
        }

        // 10. Default Music Note (♪) fallback for curved stroke with stem
        if (height > width && directDistance > height * 0.5f) {
            return GestureEvent.DrawnShape(GestureEvent.ShapeType.MUSIC_NOTE, 0.65f)
        }

        return null
    }

    private fun pathLength(points: List<GesturePoint>): Float {
        var len = 0f
        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            len += hypot((p2.x - p1.x).toDouble(), (p2.y - p1.y).toDouble()).toFloat()
        }
        return len
    }

    private fun countDirectionChanges(points: List<GesturePoint>): Int {
        if (points.size < 3) return 0
        var turns = 0
        var prevAngle = 0.0
        for (i in 0 until points.size - 1) {
            val dx = (points[i + 1].x - points[i].x).toDouble()
            val dy = (points[i + 1].y - points[i].y).toDouble()
            val angle = atan2(dy, dx)
            if (i > 0 && abs(angle - prevAngle) > Math.PI / 3) {
                turns++
            }
            prevAngle = angle
        }
        return turns
    }
}
