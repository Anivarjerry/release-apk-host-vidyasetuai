package com.vidyasetuai

import android.app.Application
import android.content.Context

/**
 * Flagship Application class for VidyaSetu AI.
 * Provides global Application Context for native singletons and hardware security keystores.
 */
class VidyaSetuApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
