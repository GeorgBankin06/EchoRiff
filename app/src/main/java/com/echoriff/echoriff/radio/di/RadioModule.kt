package com.echoriff.echoriff.radio.di

import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.radio.data.RadioRepositoryImpl
import com.echoriff.echoriff.radio.domain.usecase.FetchCategoriesUseCase
import com.echoriff.echoriff.radio.domain.usecase.FetchCategoriesUseCaseImpl
import com.echoriff.echoriff.radio.presentation.RadiosViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val radioModule = module {
    single<RadioRepository> { RadioRepositoryImpl() }

    factory<FetchCategoriesUseCase> { FetchCategoriesUseCaseImpl(get()) }

    viewModel { RadiosViewModel(get()) }
}