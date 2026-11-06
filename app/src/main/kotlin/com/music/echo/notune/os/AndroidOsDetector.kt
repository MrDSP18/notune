package com.music.echo.notune.os

import android.os.Build
import echo.music.iad1tya.constants.OsPersonality

object AndroidOsDetector {
    fun detectDeviceOs(): OsPersonality {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()

        return when {
            manufacturer.contains("google") || brand.contains("google") -> OsPersonality.PIXEL
            manufacturer.contains("samsung") || brand.contains("samsung") -> OsPersonality.SAMSUNG
            manufacturer.contains("nothing") || brand.contains("nothing") -> OsPersonality.NOTHING
            manufacturer.contains("xiaomi") || brand.contains("xiaomi") || brand.contains("redmi") || brand.contains("poco") -> OsPersonality.HYPEROS
            manufacturer.contains("oneplus") || brand.contains("oneplus") -> OsPersonality.OXYGENOS
            manufacturer.contains("oppo") || brand.contains("oppo") -> OsPersonality.COLOROS
            manufacturer.contains("vivo") || brand.contains("vivo") || brand.contains("iqoo") -> OsPersonality.ORIGINOS
            manufacturer.contains("realme") || brand.contains("realme") -> OsPersonality.REALME
            manufacturer.contains("motorola") || brand.contains("motorola") || brand.contains("moto") -> OsPersonality.MOTOROLA
            manufacturer.contains("asus") || brand.contains("asus") -> OsPersonality.ZENUI
            else -> OsPersonality.NOTUNE_ORIGINAL
        }
    }
}
