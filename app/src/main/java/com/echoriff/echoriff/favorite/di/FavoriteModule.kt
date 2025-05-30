package com.echoriff.echoriff.favorite.di

import com.echoriff.echoriff.common.domain.UserPreferences
import com.echoriff.echoriff.favorite.data.FavoriteRepository
import com.echoriff.echoriff.favorite.data.FavoriteRepositoryImpl
import com.echoriff.echoriff.favorite.domain.usecase.DeleteRadioUseCase
import com.echoriff.echoriff.favorite.domain.usecase.DeleteRadioUseCaseImpl
import com.echoriff.echoriff.favorite.domain.usecase.DeleteSongUseCase
import com.echoriff.echoriff.favorite.domain.usecase.DeleteSongUseCaseImpl
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedRadiosUseCase
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedRadiosUseCaseImpl
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedSongsUseCase
import com.echoriff.echoriff.favorite.domain.usecase.FetchLikedSongsUseCaseImpl
import com.echoriff.echoriff.favorite.domain.usecase.UpdateLikedRadiosListUseCase
import com.echoriff.echoriff.favorite.domain.usecase.UpdateLikedRadiosListUseCaseImpl
import com.echoriff.echoriff.favorite.domain.usecase.UpdateLikedSongsUseCase
import com.echoriff.echoriff.favorite.domain.usecase.UpdateLikedSongsUseCaseImpl
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
    factory<UpdateLikedRadiosListUseCase> { UpdateLikedRadiosListUseCaseImpl(get()) }
    factory<DeleteRadioUseCase> { DeleteRadioUseCaseImpl(get()) }

    factory<FetchLikedSongsUseCase> { FetchLikedSongsUseCaseImpl(get()) }
    factory<UpdateLikedSongsUseCase> { UpdateLikedSongsUseCaseImpl(get()) }
    factory<DeleteSongUseCase> { DeleteSongUseCaseImpl(get()) }

    viewModel { LikedRadiosViewModel(get(), get(), get()) }
    viewModel { LikedSongsViewModel(get(), get(), get()) }
}