package com.xeflo.blessure.entitymodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey val id: Int,
    val time: Time,
    val user: Int,
    val token: String
)