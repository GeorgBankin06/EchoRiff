package com.echoriff.echoriff.register.di

import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.register.data.RegisterRepository
import com.echoriff.echoriff.register.data.RegisterRepositoryImpl
import com.echoriff.echoriff.register.domain.usecase.RegisterUseCaseImpl
import com.echoriff.echoriff.register.domain.usecase.RegisterUseCase
import com.echoriff.echoriff.register.presentation.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val registerModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { UserPreferences(androidContext()) }

    factory<RegisterRepository> { RegisterRepositoryImpl(get(), get(), get()) }
    factory<RegisterUseCase> { RegisterUseCaseImpl(get()) }

    viewModel { RegisterViewModel(get()) }
}