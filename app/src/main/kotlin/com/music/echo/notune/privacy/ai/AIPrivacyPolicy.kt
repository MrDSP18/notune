package com.music.echo.notune.privacy.ai

import com.music.echo.notune.privacy.models.AITransparencyInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIPrivacyPolicy @Inject constructor() {

    fun getTransparencyPayload(
        userPrompt: String,
        currentTrackTitle: String?,
        currentArtistName: String?,
        isCloudAi: Boolean,
        isPrivateAiChat: Boolean
    ): AITransparencyInfo {
        val shared = mutableListOf<String>()
        val excluded = mutableListOf<String>()

        if (userPrompt.isNotBlank()) shared.add("Your prompt query")
        if (!currentTrackTitle.isNullOrBlank()) shared.add("Current track title ('$currentTrackTitle')")
        if (!currentArtistName.isNullOrBlank()) shared.add("Current artist ('$currentArtistName')")

        excluded.add("Full listening history")
        excluded.add("Account credentials & passwords")
        excluded.add("Private messages & Couple dedications")
        excluded.add("Private room keys & encryption secrets")
        if (isPrivateAiChat) {
            excluded.add("Conversation storage (Private AI Chat enabled)")
        }

        val mode = if (isCloudAi) "Cloud AI Processing (Data Minimized)" else "Local On-Device Processing"

        return AITransparencyInfo(
            sharedFields = shared,
            excludedFields = excluded,
            processingMode = mode
        )
    }
}
