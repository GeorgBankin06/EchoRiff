package com.echoriff.echoriff

import android.app.Application
import com.echoriff.echoriff.radio.di.radioModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EchoRiffApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin for dependency injection
        startKoin {
            androidContext(this@EchoRiffApplication)
            // List Koin modules here
            modules(radioModule)
        }
    }
}