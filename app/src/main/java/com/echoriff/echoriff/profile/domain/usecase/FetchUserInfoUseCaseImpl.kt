package com.echoriff.echoriff.profile.domain.usecase

import com.echoriff.echoriff.profile.data.ProfileRepository
import com.echoriff.echoriff.profile.domain.ProfileState

class FetchUserInfoUseCaseImpl(private val repository: ProfileRepository) : FetchUserInfoUseCase {
    override suspend fun fetchUserInfo(): ProfileState {
        return repository.fetchUserInfo()
    }

}