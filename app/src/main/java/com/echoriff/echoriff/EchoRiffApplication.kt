package com.echoriff.echoriff

import android.app.Application
import com.echoriff.echoriff.admin.di.adminModule
import com.echoriff.echoriff.favorite.di.favoriteModule
import com.echoriff.echoriff.login.di.loginModule
import com.echoriff.echoriff.profile.di.profileModule
import com.echoriff.echoriff.radio.di.radioModule
import com.echoriff.echoriff.register.di.registerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class EchoRiffApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin for dependency injection
        startKoin {
            androidContext(this@EchoRiffApplication)
            // List Koin modules here
            modules(radioModule)
            modules(registerModule)
            modules(loginModule)
            modules(favoriteModule)
            modules(profileModule)
            modules(adminModule)
        }
    }
}