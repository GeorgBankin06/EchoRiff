package com.echoriff.echoriff.admin.di

import com.echoriff.echoriff.admin.data.AdminRepository
import com.echoriff.echoriff.admin.data.AdminRepositoryImpl
import com.echoriff.echoriff.admin.domain.usecase.AddRadioUseCase
import com.echoriff.echoriff.admin.domain.usecase.AddRadioUseCaseImpl
import com.echoriff.echoriff.admin.domain.usecase.FetchCategoriesUseCase
import com.echoriff.echoriff.admin.domain.usecase.FetchCategoriesUseCaseImpl
import com.echoriff.echoriff.admin.presentation.AdminViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val adminModule = module {
    single<AdminRepository> { AdminRepositoryImpl() }

    factory<FetchCategoriesUseCase> { FetchCategoriesUseCaseImpl(get()) }
    factory<AddRadioUseCase> { AddRadioUseCaseImpl(get()) }

    viewModel { AdminViewModel(get(), get()) }
}