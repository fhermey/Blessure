package com.xeflo.blessure.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xeflo.blessure.entitymodels.SettingsEntity

@Database(entities = [SettingsEntity::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class SettingsDatabase: RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}