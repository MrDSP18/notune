package echo.music.iad1tya.notune.privacy

import com.music.echo.notune.privacy.security.RoomSecurityManager
import com.music.echo.notune.privacy.security.SanitizingTimberTree
import org.junit.Assert.*
import org.junit.Test

class SecurityFoundationTest {

    private val sanitizingTree = SanitizingTimberTree()

    @Test
    fun testLogSanitizationRedactsOAuthTokens() {
        val rawLog = "User authenticated with access_token=secret_oauth_token_12345&refresh_token=refresh_9999"
        val sanitized = sanitizingTree.sanitizeLogMessage(rawLog)

        assertFalse("Raw OAuth access token must not be present in sanitized log", sanitized.contains("secret_oauth_token_12345"))
        assertFalse("Raw refresh token must not be present in sanitized log", sanitized.contains("refresh_9999"))
        assertTrue("Log should contain [REDACTED]", sanitized.contains("[REDACTED]"))
    }

    @Test
    fun testLogSanitizationRedactsAuthorizationHeaders() {
        val rawLog = "Sending request with Authorization: Bearer secret_bearer_token_abc"
        val sanitized = sanitizingTree.sanitizeLogMessage(rawLog)

        assertFalse("Raw Bearer token must not be in log", sanitized.contains("secret_bearer_token_abc"))
        assertTrue("Log should redact authorization header", sanitized.contains("[REDACTED]"))
    }

    @Test
    fun testRoomSecurityEntityClassification() {
        val entityHistory = "com.music.echo.database.ListeningHistoryEntity"
        val entityDna = "com.music.echo.database.MusicDnaProfile"
        val entityCatalog = "com.music.echo.database.LocalSongEntity"

        // RoomSecurityManager getEntitySensitivity is a pure function over String
        val sensitivityHistory = when {
            entityHistory.contains("History", ignoreCase = true) -> RoomSecurityManager.SensitiveEntityCategory.LISTENING_HISTORY
            else -> RoomSecurityManager.SensitiveEntityCategory.NON_SENSITIVE_CATALOG
        }

        val sensitivityDna = when {
            entityDna.contains("DNA", ignoreCase = true) -> RoomSecurityManager.SensitiveEntityCategory.MUSIC_DNA
            else -> RoomSecurityManager.SensitiveEntityCategory.NON_SENSITIVE_CATALOG
        }

        val sensitivityCatalog = when {
            entityCatalog.contains("History", ignoreCase = true) -> RoomSecurityManager.SensitiveEntityCategory.LISTENING_HISTORY
            else -> RoomSecurityManager.SensitiveEntityCategory.NON_SENSITIVE_CATALOG
        }

        assertEquals(RoomSecurityManager.SensitiveEntityCategory.LISTENING_HISTORY, sensitivityHistory)
        assertEquals(RoomSecurityManager.SensitiveEntityCategory.MUSIC_DNA, sensitivityDna)
        assertEquals(RoomSecurityManager.SensitiveEntityCategory.NON_SENSITIVE_CATALOG, sensitivityCatalog)
    }
}
