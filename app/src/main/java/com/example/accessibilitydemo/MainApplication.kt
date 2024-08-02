package com.example.accessibilitydemo

import android.app.Application

class MainApplication:Application() {
    companion object {
        lateinit var instance: MainApplication
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
    }
}