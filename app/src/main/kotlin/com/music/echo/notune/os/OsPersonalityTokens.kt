package com.music.echo.notune.os

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import echo.music.iad1tya.constants.OsPersonality

@Immutable
data class OsPersonalityTokens(
    val name: String = "NØTUNE Original",
    val personality: OsPersonality = OsPersonality.NOTUNE_ORIGINAL,
    val cornerScale: Float = 1.0f,
    val oneHandedControlBias: Boolean = false,
    val technicalMetricsVisible: Boolean = false,
    val dotMatrixTypography: Boolean = false,
    val dynamicColorExtraction: Boolean = true,
    val motionSpeedMultiplier: Float = 1.0f,
    val glassBlurEnabled: Boolean = false,
    val headerSizeSp: Int = 22,
    val cardPaddingDp: Int = 16
)

val LocalOsPersonalityTokens = staticCompositionLocalOf { OsPersonalityTokens() }

object NotuneOsAdapter {
    fun getTokens(personality: OsPersonality): OsPersonalityTokens {
        val activeOs = if (personality == OsPersonality.AUTO) AndroidOsDetector.detectDeviceOs() else personality
        return when (activeOs) {
            OsPersonality.PIXEL -> OsPersonalityTokens(
                name = "Google Pixel UI",
                personality = OsPersonality.PIXEL,
                cornerScale = 1.5f,
                dynamicColorExtraction = true,
                motionSpeedMultiplier = 1.0f,
                glassBlurEnabled = false
            )
            OsPersonality.SAMSUNG -> OsPersonalityTokens(
                name = "Samsung One UI",
                personality = OsPersonality.SAMSUNG,
                cornerScale = 1.2f,
                oneHandedControlBias = true,
                headerSizeSp = 28,
                cardPaddingDp = 18
            )
            OsPersonality.NOTHING -> OsPersonalityTokens(
                name = "Nothing OS",
                personality = OsPersonality.NOTHING,
                cornerScale = 0.3f,
                dotMatrixTypography = true,
                technicalMetricsVisible = true,
                dynamicColorExtraction = false
            )
            OsPersonality.HYPEROS -> OsPersonalityTokens(
                name = "Xiaomi HyperOS",
                personality = OsPersonality.HYPEROS,
                cornerScale = 1.3f,
                glassBlurEnabled = true,
                motionSpeedMultiplier = 1.1f
            )
            OsPersonality.OXYGENOS -> OsPersonalityTokens(
                name = "OnePlus OxygenOS",
                personality = OsPersonality.OXYGENOS,
                cornerScale = 1.0f,
                motionSpeedMultiplier = 1.4f
            )
            OsPersonality.COLOROS -> OsPersonalityTokens(
                name = "OPPO ColorOS",
                personality = OsPersonality.COLOROS,
                cornerScale = 1.4f,
                glassBlurEnabled = true,
                dynamicColorExtraction = true
            )
            OsPersonality.ORIGINOS -> OsPersonalityTokens(
                name = "vivo OriginOS",
                personality = OsPersonality.ORIGINOS,
                cornerScale = 1.2f,
                motionSpeedMultiplier = 1.2f
            )
            OsPersonality.REALME -> OsPersonalityTokens(
                name = "realme UI",
                personality = OsPersonality.REALME,
                cornerScale = 1.1f,
                cardPaddingDp = 14
            )
            OsPersonality.MOTOROLA -> OsPersonalityTokens(
                name = "Motorola Hello UI",
                personality = OsPersonality.MOTOROLA,
                cornerScale = 1.0f,
                oneHandedControlBias = true
            )
            OsPersonality.ZENUI -> OsPersonalityTokens(
                name = "ASUS ZenUI",
                personality = OsPersonality.ZENUI,
                cornerScale = 0.8f,
                technicalMetricsVisible = true,
                motionSpeedMultiplier = 1.3f
            )
            else -> OsPersonalityTokens(
                name = "NØTUNE Original",
                personality = OsPersonality.NOTUNE_ORIGINAL
            )
        }
    }
}
