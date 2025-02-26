package com.echoriff.echoriff.favorite.di

import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.favorite.data.FavoriteRepository
import com.echoriff.echoriff.favorite.data.FavoriteRepositoryImpl
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedRadiosUseCase
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedRadiosUseCaseImpl
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedSongsUseCase
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedSongsUseCaseImpl
import com.echoriff.echoriff.favorite.presentation.LikedRadiosViewModel
import com.echoriff.echoriff.favorite.presentation.LikedSongsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favoriteModule = module {
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { UserPreferences(androidContext()) }

    single<FavoriteRepository> { FavoriteRepositoryImpl(get(), get()) }

    factory<FetchLikedRadiosUseCase> { FetchLikedRadiosUseCaseImpl(get()) }
    factory<FetchLikedSongsUseCase> { FetchLikedSongsUseCaseImpl(get()) }

    viewModel { LikedRadiosViewModel(get()) }
    viewModel { LikedSongsViewModel(get()) }
}