package com.echoriff.echoriff.admin.domain


sealed class ChangeUserRoleState {
    object Loading : ChangeUserRoleState()
    data class Success(val message: String) : ChangeUserRoleState()
    data class Failure(val error: String) : ChangeUserRoleState()
}