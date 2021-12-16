package com.xeflo.blessure.entitymodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int,
    var dashboardDays: Int,
)