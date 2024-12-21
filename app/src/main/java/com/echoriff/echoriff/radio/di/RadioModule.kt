package com.echoriff.echoriff.radio.di

import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.radio.data.RadioRepositoryImpl
import com.echoriff.echoriff.radio.domain.usecase.FetchCategoriesUseCase
import com.echoriff.echoriff.radio.domain.usecase.FetchCategoriesUseCaseImpl
import com.echoriff.echoriff.radio.presentation.PlayerViewModel
import com.echoriff.echoriff.radio.presentation.RadiosViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val radioModule = module {
    single<RadioRepository> { RadioRepositoryImpl() }
    single { UserPreferences(androidContext()) }
    factory<FetchCategoriesUseCase> { FetchCategoriesUseCaseImpl(get()) }

    viewModel { PlayerViewModel(get()) }

    viewModel { RadiosViewModel(get()) }
}