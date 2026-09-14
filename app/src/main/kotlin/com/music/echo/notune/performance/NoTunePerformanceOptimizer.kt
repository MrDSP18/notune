package com.music.echo.notune.performance

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

object NoTunePerformanceOptimizer {
    private val bitmapCache = ConcurrentHashMap<String, WeakReference<Bitmap>>()
    private var lastFrameTimestamp = 0L
    private var frameCount = 0
    private var currentFps = 60.0f

    fun cacheBitmap(key: String, bitmap: Bitmap) {
        bitmapCache[key] = WeakReference(bitmap)
    }

    fun getCachedBitmap(key: String): Bitmap? {
        return bitmapCache[key]?.get()
    }

    fun clearUnusedCache() {
        val iterator = bitmapCache.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.get() == null) {
                iterator.remove()
            }
        }
    }

    fun recordFrame() {
        val now = SystemClock.elapsedRealtime()
        frameCount++
        if (now - lastFrameTimestamp >= 1000) {
            currentFps = (frameCount * 1000f) / (now - lastFrameTimestamp)
            frameCount = 0
            lastFrameTimestamp = now
        }
    }

    fun getCurrentFps(): Float = if (currentFps <= 0f) 60f else currentFps
}

@Composable
fun rememberPerformanceMonitor(): Float {
    DisposableEffect(Unit) {
        onDispose {
            NoTunePerformanceOptimizer.clearUnusedCache()
        }
    }
    return remember { NoTunePerformanceOptimizer.getCurrentFps() }
}
