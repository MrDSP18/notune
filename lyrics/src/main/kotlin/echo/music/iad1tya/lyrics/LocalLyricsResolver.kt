package echo.music.iad1tya.lyrics

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.net.URLDecoder

object LocalLyricsResolver {

    private const val TAG = "LocalLyricsResolver"

    suspend fun resolveLocalLyrics(context: Context, audioUriOrPath: String): String? = withContext(Dispatchers.IO) {
        if (audioUriOrPath.isBlank()) return@withContext null

        try {
            // 1. Try finding companion .lrc file in file system
            val localLrcContent = findCompanionLrcFile(audioUriOrPath)
            if (!localLrcContent.isNullOrBlank()) {
                Timber.tag(TAG).d("Found companion .lrc file for $audioUriOrPath")
                return@withContext localLrcContent
            }

            // 2. Try extracting embedded lyrics from audio metadata tags
            val embeddedLyrics = extractEmbeddedLyrics(context, audioUriOrPath)
            if (!embeddedLyrics.isNullOrBlank()) {
                Timber.tag(TAG).d("Found embedded lyrics for $audioUriOrPath")
                return@withContext embeddedLyrics
            }

            null
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error resolving local lyrics for $audioUriOrPath")
            null
        }
    }

    private fun findCompanionLrcFile(audioUriOrPath: String): String? {
        val filePath = when {
            audioUriOrPath.startsWith("/") -> audioUriOrPath
            audioUriOrPath.startsWith("file://") -> audioUriOrPath.removePrefix("file://")
            audioUriOrPath.startsWith("content://") -> {
                val decoded = URLDecoder.decode(audioUriOrPath, "UTF-8")
                val relativePath = decoded.substringAfter("primary:", "")
                if (relativePath.isNotBlank()) {
                    val root = android.os.Environment.getExternalStorageDirectory().path
                    "$root/$relativePath"
                } else null
            }
            else -> null
        } ?: return null

        val audioFile = File(filePath)
        if (!audioFile.exists()) return null

        val lrcFile = File(audioFile.parent, "${audioFile.nameWithoutExtension}.lrc")
        if (lrcFile.exists() && lrcFile.length() > 0) {
            return lrcFile.readText(Charsets.UTF_8)
        }

        return null
    }

    private fun extractEmbeddedLyrics(context: Context, audioUriOrPath: String): String? {
        val retriever = MediaMetadataRetriever()
        try {
            if (audioUriOrPath.startsWith("content://")) {
                retriever.setDataSource(context, audioUriOrPath.toUri())
            } else {
                val path = audioUriOrPath.removePrefix("file://")
                retriever.setDataSource(path)
            }

            val lyrics = retriever.extractMetadata(1000)
            if (lyrics != null && (lyrics.contains("\n") || lyrics.contains("["))) {
                return lyrics
            }
        } catch (e: Exception) {
            // Ignored
        } finally {
            try { retriever.release() } catch (e: Exception) { /* Ignored */ }
        }
        return null
    }
}
