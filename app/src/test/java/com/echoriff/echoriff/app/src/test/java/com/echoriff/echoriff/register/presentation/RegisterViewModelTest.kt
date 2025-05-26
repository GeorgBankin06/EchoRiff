package com.echoriff.echoriff.app.src.test.java.com.echoriff.echoriff.register.presentation

import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.register.domain.RegisterState
import com.echoriff.echoriff.register.domain.usecase.RegisterUseCase
import com.echoriff.echoriff.register.presentation.RegisterViewModel
import io.mockk.coEvery
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
class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel
    private var registerUseCase: RegisterUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        registerUseCase = mockk()
        viewModel = RegisterViewModel(registerUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `registerUser emits Loading then Success`() = runTest {
        val user = User(
            userId = "1",
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com"
        )
        val expectedState = RegisterState.Success(user)

        coEvery {
            registerUseCase.registerUser(user.firstName, user.lastName, user.email, "test@123")
        } coAnswers {
            delay(10)
            expectedState
        }

        val stateList = mutableListOf<RegisterState>()
        val job = launch {
            viewModel.registerState.toList(stateList)
        }

        viewModel.registerUser(user.firstName, user.lastName, user.email, "test@123")

        advanceUntilIdle()

        assertEquals(RegisterState.Loading, stateList[0])
        assertEquals(expectedState, stateList[1])

        job.cancel()
    }

    @Test
    fun `registerUser emits Loading then Failure`() = runTest {
        val expectedState = RegisterState.Failure("Email already exists")

        coEvery {
            registerUseCase.registerUser("Jane", "Smith", "jane@example.com", "password123")
        } returns expectedState

        val stateList = mutableListOf<RegisterState>()
        val job = launch {
            viewModel.registerState.toList(stateList)
        }

        viewModel.registerUser("Jane", "Smith", "jane@example.com", "password123")


        advanceUntilIdle()

        assertEquals(RegisterState.Loading, stateList[0])
        assertEquals(expectedState, stateList[1])

        job.cancel()
    }
}