package com.xeflo.blessure.databases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.xeflo.blessure.entitymodels.BloodPressureEntity

@Dao
interface BloodPressureDao {
    @Query("select * from blood_pressure")
    fun getList(): List<BloodPressureEntity>

    @Query("select * from blood_pressure where id=:id")
    fun getById(id: Int): BloodPressureEntity

    @Query("delete from blood_pressure")
    fun deleteAll()

    @Insert
    fun insert(bloodPressure: BloodPressureEntity)

    @Delete
    fun delete(bloodPressure: BloodPressureEntity)
}