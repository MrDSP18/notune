package com.music.echo.notune.audio

import javax.inject.Inject
import javax.inject.Singleton

enum class AudioOutputProfile(val deviceName: String, val bassBoost: Int, val vocalClarity: Int, val spatialAudio: Boolean) {
    AIRPODS("AirPods / Wireless", bassBoost = 2, vocalClarity = 2, spatialAudio = true),
    IEM("In-Ear Monitors (IEM)", bassBoost = 0, vocalClarity = 4, spatialAudio = false),
    CAR("Car Audio System", bassBoost = 4, vocalClarity = 1, spatialAudio = false),
    SPEAKER("Bluetooth Speaker", bassBoost = 3, vocalClarity = 2, spatialAudio = false),
    DEFAULT("Default Output", bassBoost = 0, vocalClarity = 0, spatialAudio = false)
}

@Singleton
class HeadphoneIntelligenceManager @Inject constructor() {

    private var activeProfile: AudioOutputProfile = AudioOutputProfile.DEFAULT

    fun getActiveProfile(): AudioOutputProfile = activeProfile

    fun setProfile(profile: AudioOutputProfile) {
        activeProfile = profile
    }

    fun detectProfileFromDeviceName(deviceName: String): AudioOutputProfile {
        val nameLower = deviceName.lowercase()
        val profile = when {
            nameLower.contains("airpod") || nameLower.contains("bud") || nameLower.contains("tws") -> AudioOutputProfile.AIRPODS
            nameLower.contains("iem") || nameLower.contains("monitor") -> AudioOutputProfile.IEM
            nameLower.contains("car") || nameLower.contains("auto") || nameLower.contains("vehicle") -> AudioOutputProfile.CAR
            nameLower.contains("speaker") || nameLower.contains("jbl") || nameLower.contains("bose") -> AudioOutputProfile.SPEAKER
            else -> AudioOutputProfile.DEFAULT
        }
        activeProfile = profile
        return profile
    }
}
