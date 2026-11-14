package echo.music.iad1tya.notune.auth

import com.music.echo.notune.auth.NoTuneAuthManager
import com.music.echo.notune.identity.AuthProviderType
import com.music.echo.notune.identity.NotuneAccountRepository
import com.music.echo.notune.identity.NotuneUserAccount
import com.music.echo.notune.privacy.security.SecureStorageManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NoTuneAuthManagerTest {

    private lateinit var secureStorageManager: SecureStorageManager
    private lateinit var accountRepository: NotuneAccountRepository
    private lateinit var authManager: NoTuneAuthManager
    private val accountFlow = MutableStateFlow(
        NotuneUserAccount(
            userId = "test_user",
            displayName = "Test User",
            provider = AuthProviderType.OFFLINE_GUEST
        )
    )

    @Before
    fun setUp() {
        secureStorageManager = mockk(relaxed = true)
        accountRepository = mockk(relaxed = true)
        every { accountRepository.account } returns accountFlow
        authManager = NoTuneAuthManager(secureStorageManager, accountRepository)
    }

    @Test
    fun testGetActiveAccessTokenRetrievesFromSecureStorage() {
        every { secureStorageManager.getSecret(NoTuneAuthManager.KEY_ACCESS_TOKEN) } returns "mock_jwt_token_123"
        assertEquals("mock_jwt_token_123", authManager.getActiveAccessToken())
    }

    @Test
    fun testIsAuthenticatedReturnsFalseWhenNoToken() {
        every { secureStorageManager.getSecret(NoTuneAuthManager.KEY_ACCESS_TOKEN) } returns null
        assertFalse(authManager.isAuthenticated())
    }

    @Test
    fun testIsAuthenticatedReturnsTrueWhenTokenExistsAndProviderMatches() {
        every { secureStorageManager.getSecret(NoTuneAuthManager.KEY_ACCESS_TOKEN) } returns "mock_jwt_token_123"
        accountFlow.value = NotuneUserAccount(
            userId = "user_456",
            displayName = "Authenticated User",
            provider = AuthProviderType.NOTUNE_ACCOUNT,
            isAuthenticated = true
        )
        assertTrue(authManager.isAuthenticated())
    }

    @Test
    fun testLogoutClearsSecretsAndResetsAccount() = runTest {
        every { secureStorageManager.getSecret(NoTuneAuthManager.KEY_REFRESH_TOKEN) } returns null
        val result = authManager.logout()

        assertTrue(result)
        verify { secureStorageManager.removeSecret(NoTuneAuthManager.KEY_ACCESS_TOKEN) }
        verify { secureStorageManager.removeSecret(NoTuneAuthManager.KEY_REFRESH_TOKEN) }
        verify { accountRepository.signOut() }
    }
}
