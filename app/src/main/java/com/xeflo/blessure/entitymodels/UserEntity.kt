package com.xeflo.blessure.entitymodels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int,
    val firstname: String,
    val lastname: String,
    val email: String
)