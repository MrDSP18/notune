package echo.music.iad1tya.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

data class TestQueuePayload(
    val title: String,
    val mediaItemIndex: Int,
    val position: Long
) : Serializable

class QueuePersistenceAtomicTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun atomicWriteObject(dir: File, fileName: String, obj: Serializable) {
        val targetFile = File(dir, fileName)
        val tempFile = File(dir, "$fileName.tmp")
        FileOutputStream(tempFile).use { fos ->
            ObjectOutputStream(fos).use { oos ->
                oos.writeObject(obj)
                oos.flush()
            }
        }
        if (tempFile.exists()) {
            if (targetFile.exists()) targetFile.delete()
            tempFile.renameTo(targetFile)
        }
    }

    private fun recoverAndReadObject(dir: File, fileName: String): TestQueuePayload? {
        val targetFile = File(dir, fileName).let {
            val tmp = File(dir, "$fileName.tmp")
            if (!it.exists() && tmp.exists()) {
                tmp.renameTo(it)
                it
            } else it
        }
        if (!targetFile.exists()) return null
        return runCatching {
            FileInputStream(targetFile).use { fis ->
                ObjectInputStream(fis).use { ois ->
                    ois.readObject() as TestQueuePayload
                }
            }
        }.getOrNull()
    }

    @Test
    fun testAtomicWriteCreatesTargetFileCleanly() {
        val rootDir = tempFolder.root
        val payload = TestQueuePayload("My Playlist", 2, 45000L)

        atomicWriteObject(rootDir, "test_queue.data", payload)

        val target = File(rootDir, "test_queue.data")
        val tmp = File(rootDir, "test_queue.data.tmp")

        assertTrue(target.exists())
        assertFalse(tmp.exists()) // Temp file should be renamed away

        val read = recoverAndReadObject(rootDir, "test_queue.data")
        assertEquals("My Playlist", read?.title)
        assertEquals(2, read?.mediaItemIndex)
        assertEquals(45000L, read?.position)
    }

    @Test
    fun testRecoveryFromTempFileWhenTargetMissing() {
        val rootDir = tempFolder.root
        val payload = TestQueuePayload("Recovered Queue", 5, 120000L)

        // Simulate crash right before rename: write temp file directly
        val tmp = File(rootDir, "test_queue.data.tmp")
        FileOutputStream(tmp).use { fos ->
            ObjectOutputStream(fos).use { oos ->
                oos.writeObject(payload)
                oos.flush()
            }
        }

        val target = File(rootDir, "test_queue.data")
        assertFalse(target.exists())
        assertTrue(tmp.exists())

        // Recovery logic should promote .tmp to target file
        val read = recoverAndReadObject(rootDir, "test_queue.data")
        assertEquals("Recovered Queue", read?.title)
        assertEquals(5, read?.mediaItemIndex)
        assertEquals(120000L, read?.position)
        assertTrue(target.exists())
    }

    @Test
    fun testCorruptedFileReturnsNullSafely() {
        val rootDir = tempFolder.root
        val target = File(rootDir, "test_queue.data")
        target.writeBytes(byteArrayOf(0x01, 0x02, 0x03, 0x04)) // Garbled bytes

        val read = recoverAndReadObject(rootDir, "test_queue.data")
        assertEquals(null, read)
    }
}
