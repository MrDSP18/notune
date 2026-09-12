package com.music.echo.notune.memory

import echo.music.iad1tya.db.DatabaseDao
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.flow.first
import java.time.ZoneOffset
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

data class TimeMachineEra(
    val year: Int,
    val eraLabel: String,
    val songs: List<Song>
)

@Singleton
class TimeMachineEngine @Inject constructor(
    private val databaseDao: DatabaseDao
) {
    suspend fun getEras(): List<TimeMachineEra> {
        val historyEvents = databaseDao.events().first()
        if (historyEvents.isEmpty()) return emptyList()

        val eraMap = mutableMapOf<Int, MutableList<Song>>()

        for (event in historyEvents) {
            val timestamp = event.event.timestamp.toInstant(ZoneOffset.UTC).toEpochMilli()
            val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
            val year = cal.get(Calendar.YEAR)
            val list = eraMap.getOrPut(year) { mutableListOf() }
            if (list.none { it.song.id == event.song.song.id }) {
                list.add(event.song)
            }
        }

        return eraMap.map { (year, songs) ->
            TimeMachineEra(
                year = year,
                eraLabel = "My $year Era",
                songs = songs.take(20)
            )
        }.sortedByDescending { it.year }
    }
}
