package com.music.echo.notune.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import echo.music.iad1tya.constants.ThemePreset
import echo.music.iad1tya.constants.WidgetStyle

class NotuneWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, WidgetStyle.MINIMAL)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            style: WidgetStyle,
            title: String = "NØTUNE Flow",
            artist: String = "AI Audio Engine",
            isPlaying: Boolean = false,
            themePreset: ThemePreset = ThemePreset.NOTHING
        ) {
            NotuneWidgetCatalog.renderWidget(
                context = context,
                appWidgetManager = appWidgetManager,
                appWidgetId = appWidgetId,
                widgetStyle = style,
                themePreset = themePreset,
                title = title,
                artist = artist,
                artwork = null,
                isPlaying = isPlaying,
                isLiked = false
            )
        }
    }
}
