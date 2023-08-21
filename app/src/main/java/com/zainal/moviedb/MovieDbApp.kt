package com.zainal.moviedb

import android.app.Application
import com.zainal.moviedb.di.AppModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MovieDbApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MovieDbApp)
            modules(AppModules.module)
        }
    }
}