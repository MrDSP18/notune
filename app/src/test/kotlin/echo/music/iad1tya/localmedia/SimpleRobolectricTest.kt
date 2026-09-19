package echo.music.iad1tya.localmedia

import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.assertNotNull
import android.content.Context

@RunWith(RobolectricTestRunner::class)
class SimpleRobolectricTest {

    companion object {
        @org.junit.BeforeClass
        @JvmStatic
        fun setupConscryptWorkdir() {
            val workdir = java.io.File("build/tmp/conscrypt").apply { mkdirs() }
            System.setProperty("org.conscrypt.native.workdir", workdir.absolutePath)
        }
    }

    @Test
    fun testContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull(context)
    }
}

