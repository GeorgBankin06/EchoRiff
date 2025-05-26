package com.echoriff.echoriff.profile.domain.usecase

import com.echoriff.echoriff.profile.data.ProfileRepository
import com.echoriff.echoriff.profile.domain.ProfileState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchUserInfoUseCaseImpl(private val repository: ProfileRepository) : FetchUserInfoUseCase {
    override suspend fun fetchUserInfo(): ProfileState {
        return withContext(Dispatchers.IO){
            try {
                repository.fetchUserInfo()
            }catch (e: Exception){
                ProfileState.Failure(e.message ?: "Unknown error")
            }
        }
    }
}