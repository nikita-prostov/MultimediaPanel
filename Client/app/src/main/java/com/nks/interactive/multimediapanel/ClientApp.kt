package com.nks.interactive.multimediapanel

import android.app.Application
import com.nks.interactive.multimediapanel.koin.musicPlayerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ClientApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ClientApp)
            modules(musicPlayerModule)
        }
    }
}