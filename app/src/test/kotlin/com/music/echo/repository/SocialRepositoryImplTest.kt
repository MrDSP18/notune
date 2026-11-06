package com.music.echo.repository

import com.music.echo.notune.identity.NotuneAccountRepository
import com.music.echo.notune.identity.NotuneUserAccount
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.daos.SocialDao
import echo.music.iad1tya.db.entities.FriendshipEntity
import echo.music.iad1tya.db.entities.PendingSocialActionEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SocialRepositoryImplTest {

    private val database = mockk<MusicDatabase>()
    private val socialDao = mockk<SocialDao>(relaxed = true)
    private val accountRepository = mockk<NotuneAccountRepository>()
    private val repository = SocialRepositoryImpl(
        database = database,
        accountRepository = accountRepository,
        scope = mockk(relaxed = true)
    )

    init {
        every { database.socialDao } returns socialDao
        every { accountRepository.account } returns MutableStateFlow(
            NotuneUserAccount(userId = "current-user", displayName = "Current User")
        )
    }

    @Test
    fun `blockUser replaces the relationship and queues a remote action`() = runTest {
        val result = repository.blockUser("blocked-user")

        assertTrue(result.isSuccess)
        coVerify { socialDao.deleteFriendship("current-user", "blocked-user") }
        coVerify {
            socialDao.insertFriendship(
                match<FriendshipEntity> {
                    it.id == "current-user_blocked-user" &&
                        it.user1Id == "current-user" &&
                        it.user2Id == "blocked-user" &&
                        it.status == "BLOCKED"
                }
            )
        }
        coVerify {
            socialDao.insertPendingAction(
                match<PendingSocialActionEntity> {
                    it.actionType == "BLOCK_USER" && it.targetId == "blocked-user"
                }
            )
        }
    }

    @Test
    fun `unlikePost queues a remote action`() = runTest {
        val result = repository.unlikePost("post-123")

        assertTrue(result.isSuccess)
        coVerify {
            socialDao.insertPendingAction(
                match<PendingSocialActionEntity> {
                    it.actionType == "UNLIKE_POST" && it.targetId == "post-123"
                }
            )
        }
    }
}