package com.xeflo.blessure.apimodel

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AlarmsApi(val success: Int, val message: String, val data: List<AlarmApi>)

@JsonClass(generateAdapter = true)
class AlarmApi(
    val id: Int,
    val time: String,
    val user: Int,
    val token: String
)