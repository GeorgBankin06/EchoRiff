package com.echoriff.echoriff.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echoriff.echoriff.profile.domain.ProfileState
import com.echoriff.echoriff.profile.domain.usecase.FetchUserInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val fetchUserInfoUseCase: FetchUserInfoUseCase) : ViewModel() {

    private val _user = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val user = _user.asStateFlow()

    init {
        fetchUserInfo()
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            _user.value = fetchUserInfoUseCase.fetchUserInfo()
        }
    }

}