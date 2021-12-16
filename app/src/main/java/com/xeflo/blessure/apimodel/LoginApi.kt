package com.xeflo.blessure.apimodel

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class LoginApi(val success: Int, val message: String, val data: UserApi?)

@JsonClass(generateAdapter = true)
class UserApi(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val email: String
)