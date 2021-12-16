package com.xeflo.blessure.di

import androidx.room.Room
import com.android.volley.toolbox.Volley
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xeflo.blessure.databases.*
import com.xeflo.blessure.model.DownloadApi
import com.xeflo.blessure.model.TranslateApi
import com.xeflo.blessure.model.UploadApi
import com.xeflo.blessure.shared.BloodPressureHelper
import com.xeflo.blessure.viewmodel.AlarmListViewModel
import com.xeflo.blessure.viewmodel.BloodPressureListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val bloodPressureModule = module {
    viewModel { BloodPressureListViewModel(get(), get()) }

    viewModel { AlarmListViewModel(get(), get()) }

    single {
        Volley.newRequestQueue(get())
    }

    single {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    single {
        DownloadApi(get())
    }

    single {
        TranslateApi(get())
    }

    single {
        UploadApi(get())
    }

    single {
        BloodPressureHelper(get())
    }

    single{
        Room.databaseBuilder(
            get(),
            BloodPressureDatabase::class.java, "blood_pressure_db"
        ).allowMainThreadQueries().build()
    }

    single{
        Room.databaseBuilder(
            get(),
            UserDatabase::class.java, "user_db"
        ).allowMainThreadQueries().build()
    }

    single{
        Room.databaseBuilder(
            get(),
            AlarmDatabase::class.java, "alarm_db"
        ).allowMainThreadQueries().build()
    }

    single{
        Room.databaseBuilder(
            get(),
            SettingsDatabase::class.java, "settings_db"
        ).allowMainThreadQueries().build()
    }

    single {
        BloodPressureStorage(get(), get(), get(), get())
    }
}