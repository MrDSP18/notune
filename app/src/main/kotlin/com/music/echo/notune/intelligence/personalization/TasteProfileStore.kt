package com.music.echo.notune.intelligence.personalization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe state holder & manager for NØTUNE Multidimensional User DNA.
 */
@Singleton
class TasteProfileStore @Inject constructor(
    private val preferenceDecay: PreferenceDecay
) {

    private val _userDna = MutableStateFlow(NotuneUserDNA())
    val userDna: StateFlow<NotuneUserDNA> = _userDna.asStateFlow()

    fun getDnaSnapshot(): NotuneUserDNA = _userDna.value

    fun updateDna(transform: (NotuneUserDNA) -> NotuneUserDNA) {
        _userDna.update { current ->
            transform(current).copy(lastUpdatedMs = System.currentTimeMillis())
        }
    }

    /**
     * Toggles whether the current session should be excluded from long-term recommendations.
     * Prevents temporary listening (e.g. playing kids songs for siblings) from corrupting Core Taste.
     */
    fun setSessionExcludedFromTaste(excluded: Boolean) {
        _userDna.update { current ->
            current.copy(
                sessionTaste = current.sessionTaste.copy(isExcludedSession = excluded)
            )
        }
    }

    /**
     * Adds an explicit rule parsed from "Teach NØTUNE".
     */
    fun addPreferenceRule(rule: PreferenceRule) {
        _userDna.update { current ->
            val updatedRules = current.activeRules.filterNot { it.id == rule.id } + rule
            current.copy(activeRules = updatedRules)
        }
    }

    /**
     * Removes a rule by ID.
     */
    fun removePreferenceRule(ruleId: String) {
        _userDna.update { current ->
            current.copy(activeRules = current.activeRules.filterNot { it.id == ruleId })
        }
    }

    /**
     * Appends an artist/song to never recommend.
     */
    fun addNeverRecommendArtist(artistName: String) {
        _userDna.update { current ->
            val updatedNever = current.explicitPreferences.neverRecommendArtists + artistName
            current.copy(explicitPreferences = current.explicitPreferences.copy(neverRecommendArtists = updatedNever))
        }
    }

    /**
     * Applies periodic decay to old genre and artist weights.
     */
    fun applyDecay() {
        _userDna.update { current ->
            val updatedGenres = preferenceDecay.decayPreferenceMap(current.coreTaste.genres, current.lastUpdatedMs)
            val updatedArtists = preferenceDecay.decayPreferenceMap(current.coreTaste.artists, current.lastUpdatedMs)
            current.copy(
                coreTaste = current.coreTaste.copy(
                    genres = updatedGenres,
                    artists = updatedArtists
                )
            )
        }
    }
}
