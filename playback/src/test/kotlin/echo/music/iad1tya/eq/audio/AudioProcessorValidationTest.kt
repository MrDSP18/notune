package echo.music.iad1tya.eq.audio

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import echo.music.iad1tya.playback.audio.SilenceDetectorAudioProcessor
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test

class AudioProcessorValidationTest {

    @Test
    fun testCustomEqualizerAudioProcessorValidFormat() {
        val processor = CustomEqualizerAudioProcessor()
        val format = AudioProcessor.AudioFormat(44100, 2, C.ENCODING_PCM_16BIT)
        val result = processor.configure(format)
        assertNotNull(result)
    }

    @Test(expected = AudioProcessor.UnhandledAudioFormatException::class)
    fun testCustomEqualizerAudioProcessorRejectsZeroChannels() {
        val processor = CustomEqualizerAudioProcessor()
        val format = AudioProcessor.AudioFormat(44100, 0, C.ENCODING_PCM_16BIT)
        processor.configure(format)
    }

    @Test(expected = AudioProcessor.UnhandledAudioFormatException::class)
    fun testCustomEqualizerAudioProcessorRejectsSurroundChannels() {
        val processor = CustomEqualizerAudioProcessor()
        val format = AudioProcessor.AudioFormat(48000, 6, C.ENCODING_PCM_16BIT)
        processor.configure(format)
    }

    @Test(expected = AudioProcessor.UnhandledAudioFormatException::class)
    fun testCustomEqualizerAudioProcessorRejectsZeroSampleRate() {
        val processor = CustomEqualizerAudioProcessor()
        val format = AudioProcessor.AudioFormat(0, 2, C.ENCODING_PCM_16BIT)
        processor.configure(format)
    }

    @Test(expected = AudioProcessor.UnhandledAudioFormatException::class)
    fun testAutomixDuckAudioProcessorRejectsInvalidChannels() {
        val processor = AutomixDuckAudioProcessor()
        val format = AudioProcessor.AudioFormat(44100, 0, C.ENCODING_PCM_16BIT)
        processor.configure(format)
    }

    @Test(expected = AudioProcessor.UnhandledAudioFormatException::class)
    fun testSilenceDetectorAudioProcessorRejectsInvalidChannels() {
        val processor = SilenceDetectorAudioProcessor {}
        val format = AudioProcessor.AudioFormat(44100, 0, C.ENCODING_PCM_16BIT)
        processor.configure(format)
    }
}
