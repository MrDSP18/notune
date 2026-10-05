package com.music.echo.notune.intelligence.personalization

import javax.inject.Inject
import javax.inject.Singleton

enum class InstantOverrideAction {
    MORE_LIKE_THIS,
    LESS_OF_THIS,
    CHANGE_THE_VIBE,
    SURPRISE_ME,
    KEEP_THIS_STYLE,
    DONT_PLAY_THIS_ARTIST,
    GIVE_ME_SOMETHING_NEW,
    BACK_TO_USUAL_MUSIC
}

@Singleton
class SessionOverrideEngine @Inject constructor(
    private val tasteProfileStore: TasteProfileStore
) {

    fun applyInstantOverride(
        action: InstantOverrideAction,
        currentTrackGenre: String? = null,
        currentArtistName: String? = null,
        currentTrackEnergy: Float? = null
    ) {
        tasteProfileStore.updateDna { currentDna ->
            val session = currentDna.sessionTaste
            val core = currentDna.coreTaste

            when (action) {
                InstantOverrideAction.MORE_LIKE_THIS -> {
                    currentDna.copy(
                        sessionTaste = session.copy(
                            currentGenre = currentTrackGenre ?: session.currentGenre,
                            currentArtist = currentArtistName ?: session.currentArtist,
                            currentEnergy = currentTrackEnergy ?: session.currentEnergy
                        )
                    )
                }

                InstantOverrideAction.LESS_OF_THIS -> {
                    // Lower current energy target slightly
                    val newEnergy = ((session.currentEnergy - 0.20f).coerceAtLeast(0.20f))
                    currentDna.copy(
                        sessionTaste = session.copy(currentEnergy = newEnergy)
                    )
                }

                InstantOverrideAction.CHANGE_THE_VIBE -> {
                    // Shift energy to opposite spectrum
                    val newEnergy = if (session.currentEnergy > 0.50f) 0.30f else 0.80f
                    currentDna.copy(
                        sessionTaste = session.copy(
                            currentEnergy = newEnergy,
                            currentMood = if (newEnergy > 0.50f) "Upbeat" else "Calm"
                        )
                    )
                }

                InstantOverrideAction.SURPRISE_ME, InstantOverrideAction.GIVE_ME_SOMETHING_NEW -> {
                    currentDna.copy(
                        discoveryProfile = currentDna.discoveryProfile.copy(
                            explorationRate = 0.80f,
                            noveltyTolerance = 0.85f
                        )
                    )
                }

                InstantOverrideAction.DONT_PLAY_THIS_ARTIST -> {
                    val artist = currentArtistName
                    if (!artist.isNullOrEmpty()) {
                        val updatedNever = currentDna.explicitPreferences.neverRecommendArtists + artist
                        currentDna.copy(
                            explicitPreferences = currentDna.explicitPreferences.copy(neverRecommendArtists = updatedNever)
                        )
                    } else currentDna
                }

                InstantOverrideAction.BACK_TO_USUAL_MUSIC -> {
                    val defaultGenre = core.genres.maxByOrNull { it.value }?.key
                    val defaultLang = core.languages.maxByOrNull { it.value }?.key
                    currentDna.copy(
                        sessionTaste = session.copy(
                            currentGenre = defaultGenre,
                            currentLanguage = defaultLang,
                            currentEnergy = core.energyTarget,
                            consecutiveSkips = 0
                        ),
                        discoveryProfile = currentDna.discoveryProfile.copy(
                            explorationRate = 0.30f,
                            noveltyTolerance = 0.40f
                        )
                    )
                }

                InstantOverrideAction.KEEP_THIS_STYLE -> {
                    currentDna.copy(
                        sessionTaste = session.copy(
                            currentGenre = currentTrackGenre ?: session.currentGenre,
                            currentEnergy = currentTrackEnergy ?: session.currentEnergy
                        )
                    )
                }
            }
        }
    }
}
