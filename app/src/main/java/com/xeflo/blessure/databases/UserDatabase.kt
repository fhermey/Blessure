package com.xeflo.blessure.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xeflo.blessure.entitymodels.UserEntity

@Database(entities = [UserEntity::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}