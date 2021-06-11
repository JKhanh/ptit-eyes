package com.ptit.theeyes

import android.app.Application
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieConfig
import com.ptit.theeyes.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class TheEyesApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        initTimber()
        initLottie()
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

    private fun initLottie(){
        Lottie.initialize(
            LottieConfig.Builder()
                .setEnableSystraceMarkers(true)
                .build()
        )
    }
}