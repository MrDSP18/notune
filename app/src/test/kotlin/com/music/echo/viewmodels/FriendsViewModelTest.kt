package com.music.echo.viewmodels

import echo.music.iad1tya.models.SocialUser
import echo.music.iad1tya.repository.SocialRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FriendsViewModelTest {

    private val repository = mockk<SocialRepository>()
    private val friends = MutableStateFlow<List<SocialUser>>(emptyList())
    private val requests = MutableStateFlow<List<SocialUser>>(emptyList())
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `friends state observes repository updates`() = runTest(testDispatcher) {
        every { repository.getFriends() } returns friends
        every { repository.getFriendRequests() } returns requests
        val viewModel = FriendsViewModel(repository)
        advanceUntilIdle()

        val friend = SocialUser("friend-1", "friend", "Friend", null, null)
        friends.value = listOf(friend)
        advanceUntilIdle()

        assertEquals(listOf(friend), viewModel.friends.value)
    }

    @Test
    fun `failed friend mutation is exposed to the UI`() = runTest(testDispatcher) {
        every { repository.getFriends() } returns friends
        every { repository.getFriendRequests() } returns requests
        coEvery { repository.acceptFriendRequest("friend-1") } returns Result.failure(IllegalStateException("Offline"))
        val viewModel = FriendsViewModel(repository)

        viewModel.acceptRequest("friend-1")
        advanceUntilIdle()

        coVerify { repository.acceptFriendRequest("friend-1") }
        assertEquals("Offline", viewModel.mutationError.value)
    }
}