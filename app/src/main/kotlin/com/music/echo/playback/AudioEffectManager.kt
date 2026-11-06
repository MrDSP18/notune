package com.music.echo.playback

import android.media.audiofx.LoudnessEnhancer
import timber.log.Timber

class AudioEffectManager {

    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var currentAudioSessionId: Int = -1

    fun attachAudioSession(audioSessionId: Int) {
        if (audioSessionId <= 0 || audioSessionId == currentAudioSessionId) return
        release()
        currentAudioSessionId = audioSessionId

        try {
            loudnessEnhancer = LoudnessEnhancer(audioSessionId).apply {
                enabled = false
            }
            Timber.tag("AudioEffectManager").d("Attached LoudnessEnhancer to session $audioSessionId")
        } catch (e: Exception) {
            Timber.tag("AudioEffectManager").e(e, "Failed to attach LoudnessEnhancer")
        }
    }

    fun setLoudnessGain(gainMb: Int, enabled: Boolean) {
        try {
            loudnessEnhancer?.apply {
                setTargetGain(gainMb)
                this.enabled = enabled && gainMb != 0
            }
        } catch (e: Exception) {
            Timber.tag("AudioEffectManager").e(e, "Error setting loudness gain")
        }
    }

    fun release() {
        try {
            loudnessEnhancer?.release()
            loudnessEnhancer = null
            currentAudioSessionId = -1
        } catch (e: Exception) {
            // Ignored
        }
    }
}
