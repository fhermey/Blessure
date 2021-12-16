package com.xeflo.blessure.entitymodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "blood_pressure")
data class BloodPressureEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val sys: Int,
    val dia: Int,
    val datetime: LocalDateTime
)