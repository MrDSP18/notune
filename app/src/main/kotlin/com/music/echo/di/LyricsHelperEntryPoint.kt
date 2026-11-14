

package echo.music.iad1tya.di

import echo.music.iad1tya.lyrics.LyricsHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface LyricsHelperEntryPoint {
    fun lyricsHelper(): LyricsHelper
    fun aiLyricGenerator(): com.music.echo.notune.lyrics.AiLyricGenerator
    fun realtimeLyricsTranslator(): com.music.echo.notune.lyrics.RealtimeLyricsTranslator
}
