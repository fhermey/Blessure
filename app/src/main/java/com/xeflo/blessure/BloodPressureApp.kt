package com.xeflo.blessure

import android.app.Application
import android.content.Context
import com.xeflo.blessure.di.bloodPressureModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BloodPressureApp : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BloodPressureApp)

            modules(listOf(bloodPressureModule))
        }

        context = this
    }
}