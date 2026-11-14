package com.music.echo.localmedia

import com.music.echo.notune.privacy.ai.AIRequestSanitizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DuplicateSongDetectorUnicodeTest {

    @Test
    fun testTamilSongNormalizationPreservesCharacters() {
        val tamilTitle = "கண்ணே கலைமானே"
        val normalized = DuplicateSongDetector.normalizeMetadata(tamilTitle)
        
        assertFalse("Normalized Tamil title must not be empty", normalized.isEmpty())
        assertEquals("கண்ணேகலைமானே", normalized)
    }

    @Test
    fun testHindiSongNormalizationPreservesCharacters() {
        val hindiTitle = "तुम ही हो"
        val normalized = DuplicateSongDetector.normalizeMetadata(hindiTitle)
        
        assertFalse("Normalized Hindi title must not be empty", normalized.isEmpty())
        assertEquals("तुमहीहो", normalized)
    }

    @Test
    fun testDifferentTamilSongsProduceDifferentKeys() {
        val song1 = "கண்ணே கலைமானே"
        val song2 = "தென்றல் வந்து தீண்டும் போது"
        val artist = "இளையராஜா"

        val norm1 = DuplicateSongDetector.normalizeMetadata(song1)
        val norm2 = DuplicateSongDetector.normalizeMetadata(song2)
        val normArtist = DuplicateSongDetector.normalizeMetadata(artist)

        val key1 = "$norm1|$normArtist"
        val key2 = "$norm2|$normArtist"

        assertNotEquals("Keys for different Tamil songs must never collide", key1, key2)
        assertFalse("Key must not degenerate to delimiter only", key1 == "|")
        assertFalse("Key must not degenerate to delimiter only", key2 == "|")
    }

    @Test
    fun testSanitizerRedactsEmailsPhoneNumbersAndSecrets() {
        val sanitizer = AIRequestSanitizer()
        val rawPrompt = "My email is test.user@music.com and my phone is +1-555-867-5309. Secret key: Bearer abc123def456."
        val sanitized = sanitizer.sanitizePrompt(rawPrompt)

        assertFalse("Raw email must be redacted", sanitized.contains("test.user@music.com"))
        assertTrue("Redacted email token must be present", sanitized.contains("[REDACTED_EMAIL]"))
        assertFalse("Raw phone must be redacted", sanitized.contains("+1-555-867-5309"))
        assertTrue("Redacted phone token must be present", sanitized.contains("[REDACTED_PHONE]"))
        assertFalse("Raw secret token must be redacted", sanitized.contains("Bearer abc123def456"))
        assertTrue("Redacted secret token must be present", sanitized.contains("[REDACTED_SECRET]"))
    }
}
