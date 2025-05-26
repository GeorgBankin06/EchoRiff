import com.echoriff.echoriff.login.domain.LoginState
import com.echoriff.echoriff.login.domain.LoginState.Success
import com.echoriff.echoriff.login.domain.usecase.LoginUseCase
import com.echoriff.echoriff.login.presentation.LoginViewModel
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
class LoginViewModelTest {

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // very important to reset after test
    }

    @Test
    fun `login success updates state to Loading then Success`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val userRole = "user"
        val expectedState = Success(userRole)

        coEvery { loginUseCase.login(email, password) } coAnswers {
            delay(10)
            expectedState
        }

        val stateList = mutableListOf<LoginState>()
        val job = launch {
            viewModel.loginState.toList(stateList)
        }

        viewModel.login(email, password)

        advanceUntilIdle()

        assertEquals(LoginState.Idle, stateList[0])
        assertEquals(LoginState.Loading, stateList[1])
        assertEquals(expectedState, stateList[2])

        job.cancel()
    }

    @Test
    fun `login failure updates state to Loading then Error`() = runTest {
        val email = "wrong@example.com"
        val password = "badpass"
        val expectedState = LoginState.Failure("Invalid credentials")

        coEvery { loginUseCase.login(email, password) } coAnswers {
            delay(10)
            expectedState
        }

        val stateList = mutableListOf<LoginState>()
        val job = launch {
            viewModel.loginState.toList(stateList)
        }

        viewModel.login(email, password)

        advanceUntilIdle()

        assertEquals(LoginState.Idle, stateList[0])
        assertEquals(LoginState.Loading, stateList[1])
        assertEquals(expectedState, stateList[2])

        job.cancel()
    }
}
