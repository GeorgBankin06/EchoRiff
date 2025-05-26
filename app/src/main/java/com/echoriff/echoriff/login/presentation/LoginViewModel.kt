package com.echoriff.echoriff.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echoriff.echoriff.login.domain.LoginState
import com.echoriff.echoriff.login.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase): ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

     fun login(email: String, password: String){
         viewModelScope.launch {
             _loginState.value = LoginState.Loading

             val result = loginUseCase.login(email, password)

             _loginState.value = result
         }
     }
}
