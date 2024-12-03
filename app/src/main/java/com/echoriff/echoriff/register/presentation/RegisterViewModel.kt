package com.echoriff.echoriff.register.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echoriff.echoriff.register.domain.RegisterState
import com.echoriff.echoriff.register.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Loading)
    val registerState = _registerState.asStateFlow()

    fun registerUser(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading  // Set loading state

            // Call the UseCase to register the user
            val result = registerUseCase.registerUser(firstName, lastName, email, password)

            // Update the state based on the result
            _registerState.value = result
        }
    }
}
