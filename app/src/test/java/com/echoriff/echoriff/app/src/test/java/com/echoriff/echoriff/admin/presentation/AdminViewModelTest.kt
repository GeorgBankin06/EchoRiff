package com.echoriff.echoriff.app.src.test.java.com.echoriff.echoriff.admin.presentation

import com.echoriff.echoriff.admin.domain.ChangeUserRoleState
import com.echoriff.echoriff.admin.domain.usecase.AddRadioUseCase
import com.echoriff.echoriff.admin.domain.usecase.ChangeUserRoleUseCase
import com.echoriff.echoriff.admin.domain.usecase.FetchCategoriesUseCase
import com.echoriff.echoriff.admin.presentation.AdminViewModel
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.domain.model.Radio
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {

    private lateinit var fetchCategoriesUseCase: FetchCategoriesUseCase
    private lateinit var addRadioUseCase: AddRadioUseCase
    private lateinit var changeUserRoleUseCase: ChangeUserRoleUseCase
    private lateinit var viewModel: AdminViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fetchCategoriesUseCase = mockk()
        addRadioUseCase = mockk(relaxed = true)
        changeUserRoleUseCase = mockk()
        viewModel = AdminViewModel(
            fetchCategoriesUseCase,
            addRadioUseCase,
            changeUserRoleUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // very important to reset after test
    }

    @Test
    fun `loadCategories emits categories to state flow`() = runTest {
        val categories = listOf(
            Category(title = "Rock"),
            Category(title = "Jazz")
        )

        coEvery { fetchCategoriesUseCase.fetchCategories() } coAnswers {
            delay(10)
            categories
        }

        viewModel.loadCategories()
        advanceUntilIdle()

        assertEquals(categories, viewModel.categories.value)
    }

    @Test
    fun `addRadioToCategory calls use case`() = runTest {
        val category = "Rock"
        val radio = Radio(
            title = "Rock FM",
            streamUrl = "https://stream.com",
            coverArtUrl = "https://image.com",
            intro = "Classic Rock Radio"
        )

        viewModel.addRadioToCategory(category, radio)

        advanceUntilIdle()

        coVerify(exactly = 1) { addRadioUseCase.addRadio(category, radio) }
    }

    @Test
    fun `changeUserCase emits changeUserRole state`() = runTest {
        val email = "user@example.com"
        val role = "Admin"
        val expectedState = ChangeUserRoleState.Success("Successed")

        coEvery { changeUserRoleUseCase.changeUserRole(email, role) } returns expectedState

        val result = mutableListOf<ChangeUserRoleState>()
        val job = launch {
            viewModel.changeUserRole.toList(result)
        }

        viewModel.changeUserCase(email, role)

        advanceUntilIdle()

        assertEquals(expectedState, result.first())

        job.cancel()
    }
}
