package com.ptit.theeyes

import android.app.Application
import com.ptit.theeyes.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class TheEyesApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        initTimber()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@TheEyesApplication)
            val modules = modules(
                listOf(
                    viewModelModule
                )
            )
        }
    }

    private fun initTimber() {
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}