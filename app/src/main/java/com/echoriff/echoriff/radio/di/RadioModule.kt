package com.echoriff.echoriff.radio.di

import com.echoriff.echoriff.radio.data.RadioRepository
import com.echoriff.echoriff.radio.data.RadioRepositoryImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val radioModule = module {
    single<RadioRepository> { RadioRepositoryImpl() }

}