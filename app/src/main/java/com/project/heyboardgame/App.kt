package com.project.heyboardgame

import android.app.Application
import android.content.Context
import timber.log.Timber

class App : Application() {

    init {
        instance = this
    }

    companion object {

        private var instance : App? = null

        fun getContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}