package com.xeflo.blessure.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xeflo.blessure.entitymodels.AlarmEntity

@Database(entities = [AlarmEntity::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class AlarmDatabase: RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}