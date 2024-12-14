package com.echoriff.echoriff.login.di

import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.login.data.LoginRepository
import com.echoriff.echoriff.login.data.LoginRepositoryImpl
import com.echoriff.echoriff.login.domain.usecase.LoginUseCase
import com.echoriff.echoriff.login.domain.usecase.LoginUseCaseImpl
import com.echoriff.echoriff.login.presentation.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { UserPreferences(androidContext()) }

    factory<LoginRepository> { LoginRepositoryImpl(get(), get()) }
    factory<LoginUseCase> { LoginUseCaseImpl(get()) }
    viewModel { LoginViewModel(get()) }
}