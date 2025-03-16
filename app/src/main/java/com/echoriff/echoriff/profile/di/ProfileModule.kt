package com.echoriff.echoriff.profile.di

import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.profile.data.ProfileRepository
import com.echoriff.echoriff.profile.data.ProfileRepositoryImpl
import com.echoriff.echoriff.profile.domain.usecase.FetchUserInfoUseCase
import com.echoriff.echoriff.profile.domain.usecase.FetchUserInfoUseCaseImpl
import com.echoriff.echoriff.profile.domain.usecase.UpdateUserInfoUseCase
import com.echoriff.echoriff.profile.domain.usecase.UpdateUserInfoUseCaseImpl
import com.echoriff.echoriff.profile.presentation.ProfileViewModel
import com.echoriff.echoriff.radio.domain.usecase.FetchCategoriesUseCaseImpl
import com.echoriff.echoriff.radio.presentation.PlayerViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { UserPreferences(androidContext()) }

    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }

    factory<FetchUserInfoUseCase> { FetchUserInfoUseCaseImpl(get()) }
    factory<UpdateUserInfoUseCase> { UpdateUserInfoUseCaseImpl(get()) }

    viewModel { ProfileViewModel(get(), get()) }
}