package com.xeflo.blessure.model

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.xeflo.blessure.datamodels.BloodPressure
import org.json.JSONObject
import com.android.volley.AuthFailureError
import com.xeflo.blessure.datamodels.Alarm

import kotlin.collections.HashMap


class UploadApi(private val requestQueue: RequestQueue) {
    fun addBloodPressure(bloodPressure: BloodPressure): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("sys", bloodPressure.sys)
        jsonObject.put("dia", bloodPressure.dia)
        jsonObject.put("datetime", bloodPressure.datetime.toString())
        jsonObject.put("user", bloodPressure.userId)

        return RequestFuture.newFuture<JSONObject>().apply {
            requestQueue.add(object : JsonObjectRequest(
                Request.Method.POST,
                "https://xeflo.de/php/blessure/blood_pressure_add.php",
                jsonObject,
                this, Response.ErrorListener { error ->
                    Log.e("UploadApi::addBloodPressure", "received an error: $error");
                }

            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["User-agent"] = "My useragent"
                    return headers
                }
            }.also {
                setRequest(it)
            })
        }.get()
    }

    fun removeBloodPressure(bloodPressure: BloodPressure): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("id", bloodPressure.id)
        jsonObject.put("user", bloodPressure.userId)

        return RequestFuture.newFuture<JSONObject>().apply {
            requestQueue.add(object : JsonObjectRequest(
                Request.Method.POST,
                "https://xeflo.de/php/blessure/blood_pressure_delete.php",
                jsonObject,
                this, Response.ErrorListener { error ->
                    Log.e("UploadApi::deleteBloodPressure", "received an error: $error");
                }

            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    return headers
                }
            }.also {
                setRequest(it)
            })
        }.get()
    }

    fun addAlarm(alarm: Alarm): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("time", alarm.time)
        jsonObject.put("user", alarm.user)
        jsonObject.put("token", alarm.token)

        return RequestFuture.newFuture<JSONObject>().apply {
            requestQueue.add(object : JsonObjectRequest(
                Request.Method.POST,
                "https://xeflo.de/php/blessure/alarm_add.php",
                jsonObject,
                this, Response.ErrorListener { error ->
                    Log.e("UploadApi::addAlarm", "received an error: $error");
                }

            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["User-agent"] = "My useragent"
                    return headers
                }
            }.also {
                setRequest(it)
            })
        }.get()
    }

    fun updateAlarm(alarm: Alarm): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("id", alarm.id)
        jsonObject.put("time", alarm.time)
        jsonObject.put("user", alarm.user)
        jsonObject.put("token", alarm.token)

        return RequestFuture.newFuture<JSONObject>().apply {
            requestQueue.add(object : JsonObjectRequest(
                Request.Method.POST,
                "https://xeflo.de/php/blessure/alarm_update.php",
                jsonObject,
                this, Response.ErrorListener { error ->
                    Log.e("UploadApi::updateAlarm", "received an error: $error");
                }

            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["User-agent"] = "My useragent"
                    return headers
                }
            }.also {
                setRequest(it)
            })
        }.get()
    }

    fun removeAlarm(alarm: Alarm): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("id", alarm.id)
        jsonObject.put("user", alarm.user)

        return RequestFuture.newFuture<JSONObject>().apply {
            requestQueue.add(object : JsonObjectRequest(
                Request.Method.POST,
                "https://xeflo.de/php/blessure/alarm_delete.php",
                jsonObject,
                this, Response.ErrorListener { error ->
                    Log.e("UploadApi::deleteAlarm", "received an error: $error");
                }

            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    return headers
                }
            }.also {
                setRequest(it)
            })
        }.get()
    }

    fun login(user: String, password: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("email", user)
        jsonObject.put("password", password)

        return RequestFuture.newFuture<JSONObject>().apply {
            requestQueue.add(object : JsonObjectRequest(
                Request.Method.POST,
                "https://xeflo.de/php/xeflo/login.php",
                jsonObject,
                this, Response.ErrorListener { error ->
                    Log.e("UploadApi::login", "received an error: $error");
                }

            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    return headers
                }

                override fun getBodyContentType(): String? {
                    return "application/x-www-form-urlencoded;charset=UTF-8"
                }
            }.also {
                setRequest(it)
            })
        }.get()
    }
}