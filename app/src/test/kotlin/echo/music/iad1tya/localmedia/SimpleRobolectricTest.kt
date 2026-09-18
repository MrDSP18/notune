package echo.music.iad1tya.localmedia

import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.assertNotNull
import android.content.Context

@RunWith(RobolectricTestRunner::class)
class SimpleRobolectricTest {
    @Test
    fun testContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull(context)
    }
}
