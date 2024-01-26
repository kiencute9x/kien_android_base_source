package com.kiencute.basesrc

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InApplication : Application () {
    override fun onCreate() {
        super.onCreate()
    }
}