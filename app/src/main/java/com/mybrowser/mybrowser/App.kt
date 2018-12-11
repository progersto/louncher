package com.mybrowser.mybrowser

import android.app.Application
import com.mybrowser.mybrowser.di.appModules
import org.koin.android.ext.android.startKoin

class App: Application(){

    override fun onCreate() {
        super.onCreate()
        startKoin(appModules)
    }
}