package com.jesusvilla.challengewearos

import android.app.Application
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //For dynamic theming on Android 12 and above
        DynamicColors.applyToActivitiesIfAvailable(this)
        Timber.plant(Timber.DebugTree())
    }
}