package echo.music.iad1tya.notune.updater.downloadmanager

import android.content.Context
import android.os.Environment
import echo.music.iad1tya.R
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class CustomDownloadManager {
    // Use a persistent scope tied to this manager instance, not a throwaway scope that can't be cancelled.
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var downloadJob: Job? = null
    private var isPaused = false

    fun downloadApk(
        context: Context,
        apkUrl: String,
        onProgress: (Float) -> Unit,
        onDownloadComplete: (File) -> Unit,
        onError: (String) -> Unit,
    ) {
        downloadJob?.cancel()
        isPaused = false

        downloadJob = managerScope.launch {
            try {
                val url = URL(apkUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    withContext(Dispatchers.Main) {
                        onError("Server returned HTTP ${connection.responseCode}")
                    }
                    return@launch
                }

                val fileLength = connection.contentLength
                val inputStream = connection.inputStream

                val downloadDir = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "echo_updates"
                )
                if (!downloadDir.exists()) {
                    downloadDir.mkdirs()
                }

                val outputFile = File(downloadDir, "notune.apk")
                val outputStream = FileOutputStream(outputFile)

                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytesRead: Long = 0

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    if (isPaused || !isActive) {
                        outputStream.close()
                        inputStream.close()
                        connection.disconnect()
                        return@launch
                    }

                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    if (fileLength > 0) {
                        val progress = totalBytesRead.toFloat() / fileLength.toFloat()
                        withContext(Dispatchers.Main) {
                            onProgress(progress)
                        }
                    }
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()
                connection.disconnect()

                withContext(Dispatchers.Main) {
                    onDownloadComplete(outputFile)
                }
            } catch (e: CancellationException) {
                // Normal cancellation — don't report as error.
                throw e
            } catch (e: Exception) {
                Timber.e(e, "APK download failed")
                withContext(Dispatchers.Main) {
                    onError(e.message ?: context.getString(R.string.download_failed))
                }
            }
        }
    }

    fun pauseDownload() {
        isPaused = true
        downloadJob?.cancel()
    }

    fun cancelDownload() {
        isPaused = false
        downloadJob?.cancel()
        downloadJob = null
    }

    /** Call when the owning object is destroyed to prevent resource leaks. */
    fun release() {
        managerScope.cancel()
    }
}
