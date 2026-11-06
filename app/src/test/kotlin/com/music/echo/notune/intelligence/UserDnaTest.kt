package com.music.echo.notune.intelligence

import com.music.echo.notune.intelligence.personalization.CoreTaste
import com.music.echo.notune.intelligence.personalization.NotuneUserDNA
import com.music.echo.notune.intelligence.personalization.PreferenceDecay
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserDnaTest {

    private lateinit var preferenceDecay: PreferenceDecay
    private lateinit var tasteProfileStore: TasteProfileStore

    @Before
    fun setUp() {
        preferenceDecay = PreferenceDecay()
        tasteProfileStore = TasteProfileStore(preferenceDecay)
    }

    @Test
    fun `test default user DNA creation`() {
        val dna = tasteProfileStore.getDnaSnapshot()
        assertEquals("local_user", dna.userId)
        assertTrue(dna.coreTaste.genres.containsKey("Tamil Pop"))
        assertFalse(dna.sessionTaste.isExcludedSession)
    }

    @Test
    fun `test session taste exclusion toggle`() {
        tasteProfileStore.setSessionExcludedFromTaste(true)
        assertTrue(tasteProfileStore.getDnaSnapshot().sessionTaste.isExcludedSession)

        tasteProfileStore.setSessionExcludedFromTaste(false)
        assertFalse(tasteProfileStore.getDnaSnapshot().sessionTaste.isExcludedSession)
    }

    @Test
    fun `test preference decay calculation`() {
        val base = 0.8f
        val now = System.currentTimeMillis()
        val decayed = preferenceDecay.calculateDecayedWeight(base, now)
        assertEquals(0.8f, decayed, 0.05f)
    }
}
