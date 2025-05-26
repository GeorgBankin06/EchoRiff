package com.echoriff.echoriff.app.src.test.java.com.echoriff.echoriff.profile.presentation

import android.net.Uri
import androidx.core.net.toUri
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.profile.domain.EditState
import com.echoriff.echoriff.profile.domain.ProfileState
import com.echoriff.echoriff.profile.domain.usecase.FetchUserInfoUseCase
import com.echoriff.echoriff.profile.domain.usecase.UpdateUserInfoUseCase
import com.echoriff.echoriff.profile.presentation.ProfileViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var fetchUserInfoUseCase: FetchUserInfoUseCase
    private lateinit var updateUserInfoUseCase: UpdateUserInfoUseCase
    private lateinit var viewModel: ProfileViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fetchUserInfoUseCase = mockk()
        updateUserInfoUseCase = mockk()
        viewModel = ProfileViewModel(fetchUserInfoUseCase, updateUserInfoUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchUserInfo should emit ProfileState`() = runTest {
        val user = User(
            userId = "123",
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com"
        )
        val expectedState = ProfileState.Success(user)

        coEvery { fetchUserInfoUseCase.fetchUserInfo() } coAnswers {
            delay(10)
            expectedState
        }

        val stateList = mutableListOf<ProfileState>()
        val job = launch {
            viewModel.user
        }

        viewModel.fetchUserInfo()

        advanceUntilIdle()

        coVerify(exactly = 1) { fetchUserInfoUseCase.fetchUserInfo() }
    }

    @Test
    fun `updateUserInfo should emit Loading and Success`() = runTest {
        val user = User(userId = "1", firstName = "Test", lastName = "User", email = "t@e.com")
        val uri: Uri = mockk()

        val expectedEditState = EditState.Success("Success")

        coEvery { updateUserInfoUseCase.updateUserInfo(user, uri) } coAnswers {
            delay(10)
            expectedEditState
        }

        val stateList = mutableListOf<EditState>()
        val job = launch {
            viewModel.userInfo.toList(stateList)
        }

        viewModel.updateUserInfo(user, uri)

        advanceUntilIdle()

        assertEquals(EditState.Loading, stateList[0])
        assertEquals(expectedEditState, stateList[1])

        job.cancel()
    }
}