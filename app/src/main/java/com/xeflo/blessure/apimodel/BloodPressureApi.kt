package com.xeflo.blessure.apimodel

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class BloodPressuresApi(val data: Array<BloodPressureApi>)

@JsonClass(generateAdapter = true)
class BloodPressureApi(val id: Int,
                       val user: Int,
                       val sys: Int,
                       val dia: Int,
                       val datetime: String)