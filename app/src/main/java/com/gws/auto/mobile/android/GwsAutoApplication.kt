package com.gws.auto.mobile.android

import android.app.Application
import com.gws.auto.mobile.android.BuildConfig
import timber.log.Timber

class GwsAutoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.plant(FileLoggingTree(this))
        }
    }
}
