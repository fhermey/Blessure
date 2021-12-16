package com.xeflo.blessure.model

import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.RequestFuture

class DownloadApi(private val requestQueue: RequestQueue) {
    fun getBloodPressures(userId: Int): String {
        return RequestFuture.newFuture<String>().apply {
            requestQueue.add(object: StringRequest(
                Request.Method.GET,
                "https://xeflo.de/php/blessure/blood_pressure_get.php?user=$userId",
                this, this
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

    fun getAlarms(userId: Int): String {
        return RequestFuture.newFuture<String>().apply {
            requestQueue.add(object: StringRequest(
                Request.Method.GET,
                "https://xeflo.de/php/blessure/alarm_get.php?user=$userId",
                this, this
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
}