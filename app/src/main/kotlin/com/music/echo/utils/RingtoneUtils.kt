
package echo.music.iad1tya.utils

import android.content.ContentValues
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import echo.music.iad1tya.models.MediaMetadata
import java.io.File

object RingtoneUtils {
    fun setAsRingtone(context: Context, song: MediaMetadata) {
        val file = File(context.cacheDir, "${song.title}.mp3") // Simplified for local files
        if (!file.exists()) {
             Toast.makeText(context, "Download song first to set as ringtone", Toast.LENGTH_SHORT).show()
             return
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DATA, file.absolutePath)
            put(MediaStore.MediaColumns.TITLE, song.title)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
            put(MediaStore.Audio.Media.IS_RINGTONE, true)
            put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            put(MediaStore.Audio.Media.IS_ALARM, false)
            put(MediaStore.Audio.Media.IS_MUSIC, false)
        }

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val newUri = context.contentResolver.insert(uri, values)

        RingtoneManager.setActualDefaultRingtoneUri(
            context,
            RingtoneManager.TYPE_RINGTONE,
            newUri
        )
        Toast.makeText(context, "Ringtone set successfully", Toast.LENGTH_SHORT).show()
    }
}
