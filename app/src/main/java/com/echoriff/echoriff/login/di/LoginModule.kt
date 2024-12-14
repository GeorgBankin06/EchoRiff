package com.echoriff.echoriff.login.di

import com.echoriff.echoriff.login.data.LoginRepository
import com.echoriff.echoriff.login.data.LoginRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    single { FirebaseAuth.getInstance() }
    factory<LoginRepository> { LoginRepositoryImpl(get()) }
    factory<LoginUseCase> { LoginUseCaseImpl(get()) }
    viewModel { LoginViewModel(get()) }
}