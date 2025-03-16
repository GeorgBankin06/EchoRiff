package com.echoriff.echoriff.profile.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echoriff.echoriff.common.domain.User
import com.echoriff.echoriff.profile.domain.EditState
import com.echoriff.echoriff.profile.domain.ProfileState
import com.echoriff.echoriff.profile.domain.usecase.FetchUserInfoUseCase
import com.echoriff.echoriff.profile.domain.usecase.UpdateUserInfoUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
    private val updateUserInfo: UpdateUserInfoUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val user = _user.asStateFlow()

    private val _userInfo = MutableSharedFlow<EditState>()
    val userInfo = _userInfo.asSharedFlow()

    fun updateUserInfo(
        user: User,
        newProfileImageUri: Uri?,
    ) {
        viewModelScope.launch {
            _userInfo.emit(EditState.Loading)
            _userInfo.emit(updateUserInfo.updateUserInfo(user, newProfileImageUri))
        }
    }

     fun fetchUserInfo() {
        viewModelScope.launch {
            _user.value = fetchUserInfoUseCase.fetchUserInfo()
        }
    }
}