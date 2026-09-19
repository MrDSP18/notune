package com.music.echo.notune.social.repository

import com.music.echo.notune.social.model.FriendCircle
import com.music.echo.notune.social.model.MusicPoll
import com.music.echo.notune.social.model.MusicPollOption
import com.music.echo.notune.social.model.MusicStory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepository @Inject constructor() {

    private val _circles = MutableStateFlow<List<FriendCircle>>(
        listOf(
            FriendCircle("1", "🎧", "Lo-Fi Chill Squad", 5, "Late Night Beats"),
            FriendCircle("2", "🔥", "Hip-Hop Bangerz", 8, "Weekly Top Heat"),
            FriendCircle("3", "🎻", "Classical Enthusiasts", 3, "Focus & Calm")
        )
    )
    val circles: StateFlow<List<FriendCircle>> = _circles.asStateFlow()

    private val _polls = MutableStateFlow<List<MusicPoll>>(
        listOf(
            MusicPoll(
                id = "p1",
                question = "Which album deserved Album of the Year?",
                creatorName = "Alex",
                totalVotes = 42,
                isVoted = false,
                options = listOf(
                    MusicPollOption("o1", "Dawn FM", 18),
                    MusicPollOption("o2", "SOS", 14),
                    MusicPollOption("o3", "Renaissance", 10)
                )
            ),
            MusicPoll(
                id = "p2",
                question = "Best vibe for late night coding?",
                creatorName = "DevSquad",
                totalVotes = 29,
                isVoted = false,
                options = listOf(
                    MusicPollOption("o4", "Synthwave", 15),
                    MusicPollOption("o5", "Ambient Rain", 9),
                    MusicPollOption("o6", "Phonk", 5)
                )
            )
        )
    )
    val polls: StateFlow<List<MusicPoll>> = _polls.asStateFlow()

    private val _stories = MutableStateFlow<List<MusicStory>>(
        listOf(
            MusicStory("s1", "Sarah", "2h ago", "Starboy", "The Weeknd", "On repeat all morning ☕"),
            MusicStory("s2", "Marcus", "5h ago", "Blinding Lights", "The Weeknd", "Late night drive vibes 🚗"),
            MusicStory("s3", "Elena", "8h ago", "As It Was", "Harry Styles", "Weekend mood ✨")
        )
    )
    val stories: StateFlow<List<MusicStory>> = _stories.asStateFlow()

    fun votePoll(pollId: String, optionId: String) {
        val currentPolls = _polls.value
        _polls.value = currentPolls.map { poll ->
            if (poll.id == pollId && !poll.isVoted) {
                val updatedOptions = poll.options.map { opt ->
                    if (opt.id == optionId) opt.copy(votes = opt.votes + 1) else opt
                }
                poll.copy(
                    totalVotes = poll.totalVotes + 1,
                    isVoted = true,
                    options = updatedOptions
                )
            } else {
                poll
            }
        }
    }
}
