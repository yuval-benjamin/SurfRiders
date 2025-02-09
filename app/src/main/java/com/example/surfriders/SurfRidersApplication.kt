package com.example.surfriders

import android.app.Application
import android.content.Context

class SurfRidersApplication : Application() {

    object Globals {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.appContext = applicationContext
    }
}