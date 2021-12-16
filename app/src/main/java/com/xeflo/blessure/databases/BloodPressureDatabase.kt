package com.xeflo.blessure.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xeflo.blessure.entitymodels.BloodPressureEntity

@Database(entities = [BloodPressureEntity::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class BloodPressureDatabase: RoomDatabase() {
    abstract fun bloodPressureDao(): BloodPressureDao
}