package com.ignaherner.stocky

import android.app.Application
import com.ignaherner.stocky.di.AppContainer

class StockyApp : Application() {
    // Contenedor de dependencias
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}