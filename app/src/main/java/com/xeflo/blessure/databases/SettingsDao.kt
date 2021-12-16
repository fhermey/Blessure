package com.xeflo.blessure.databases

import androidx.room.*
import com.xeflo.blessure.entitymodels.SettingsEntity

@Dao
interface SettingsDao {
    @Query("select * from settings")
    fun getList(): List<SettingsEntity>

    @Query("select * from settings where id=:id")
    fun getById(id: Int): SettingsEntity

    @Query("delete from settings")
    fun deleteAll()

    @Insert
    fun insert(settings: SettingsEntity)

    @Update
    fun update(settings: SettingsEntity)

    @Delete
    fun delete(settings: SettingsEntity)
}