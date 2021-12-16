package com.xeflo.blessure.databases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.xeflo.blessure.entitymodels.AlarmEntity

@Dao
interface AlarmDao {
    @Query("select * from alarm")
    fun getList(): List<AlarmEntity>

    @Query("select * from alarm where id=:id")
    fun getById(id: Int): AlarmEntity

    @Query("delete from alarm")
    fun deleteAll()

    @Insert
    fun insert(alarm: AlarmEntity)

    @Delete
    fun delete(alarm: AlarmEntity)
}