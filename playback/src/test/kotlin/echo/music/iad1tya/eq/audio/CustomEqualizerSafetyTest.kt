package echo.music.iad1tya.eq.audio

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import echo.music.iad1tya.eq.data.ParametricEQ
import echo.music.iad1tya.eq.data.ParametricEQBand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs

class CustomEqualizerSafetyTest {

    @Test
    fun boostedSignalRemainsValidPcm16WithoutIntegerOverflow() {
        val processor = CustomEqualizerAudioProcessor()
        processor.configure(AudioProcessor.AudioFormat(48000, 2, C.ENCODING_PCM_16BIT))
        processor.applyProfile(
            ParametricEQ(
                preamp = 12.0,
                bands = listOf(
                    ParametricEQBand(
                        frequency = 1000.0,
                        gain = 18.0,
                        q = 1.0
                    )
                )
            )
        )

        val input = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder())
        repeat(8) { input.putShort(30000) }
        input.flip()

        processor.queueInput(input)
        val output = processor.getOutput()

        assertEquals(16, output.remaining())
        while (output.remaining() >= 2) {
            val sample = output.short.toInt()
            assertTrue(sample >= Short.MIN_VALUE.toInt() && sample <= Short.MAX_VALUE.toInt())
            assertTrue(abs(sample) <= 32767)
        }
    }
}
