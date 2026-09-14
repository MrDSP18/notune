package com.music.echo.notune.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import echo.music.iad1tya.R
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
            isPlaying: Boolean = false
        ) {
            val layoutId = when (style) {
                WidgetStyle.MINIMAL -> R.layout.widget_music_player
                WidgetStyle.GLASS -> R.layout.widget_compact_wide
                WidgetStyle.TECHNICAL -> R.layout.widget_compact_square
                WidgetStyle.ARTWORK -> R.layout.widget_turntable
                WidgetStyle.WAVEFORM -> R.layout.widget_music_player
                WidgetStyle.COMPACT -> R.layout.widget_compact_square
                WidgetStyle.EDITORIAL -> R.layout.widget_playlist
                WidgetStyle.PIXEL -> R.layout.widget_compact_wide
                WidgetStyle.CINEMA -> R.layout.widget_turntable
                WidgetStyle.DYNAMIC -> R.layout.widget_music_player
            }

            val views = RemoteViews(context.packageName, layoutId)
            try {
                views.setTextViewText(R.id.widget_song_title, title)
            } catch (_: Exception) {}

            try {
                views.setTextViewText(R.id.widget_artist_name, artist)
            } catch (_: Exception) {}

            val playPauseIcon = if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
            try {
                views.setImageViewResource(R.id.widget_play_pause, playPauseIcon)
            } catch (_: Exception) {}

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
