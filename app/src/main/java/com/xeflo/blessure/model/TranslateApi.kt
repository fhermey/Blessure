package com.xeflo.blessure.model

import android.content.Context
import android.util.Log
import com.xeflo.blessure.apimodel.AlarmsApi
import com.xeflo.blessure.apimodel.BloodPressuresApi
import com.xeflo.blessure.apimodel.LoginApi
import com.xeflo.blessure.datamodels.Alarm
import com.xeflo.blessure.datamodels.BloodPressure
import com.xeflo.blessure.datamodels.User
import java.sql.Time
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TranslateApi(private val context: Context) {
    fun translateBloodPressures(bloodPressuresApi: BloodPressuresApi): List<BloodPressure> {
        val list = mutableListOf<BloodPressure>().apply {
            bloodPressuresApi.data.forEach {
                Log.e("DEBUGFH30: ", it.datetime);
                var localDateTime = LocalDateTime.now()
                Log.e("DEBUGFH31: ", localDateTime.toString());
                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    localDateTime = LocalDateTime.parse(it.datetime, formatter);
                    Log.e("DEBUGFH32: ", localDateTime.toString());
                } catch (e: Exception) {}
                Log.e("DEBUGFH33: ", localDateTime.toString());
                this.add(
                    BloodPressure(
                        it.id,
                        it.user,
                        it.sys,
                        it.dia,
                        localDateTime
                        //SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.US).let {format -> format.parse(it.datetime)?: Date() },
                    )
                )

                Unit
            }
        }
        return list.sortedByDescending {it.datetime}
    }

    fun translateAlarms(alarmsApi: AlarmsApi): List<Alarm> {
        val list = mutableListOf<Alarm>().apply {
            alarmsApi.data.forEach {
                this.add(
                    Alarm(
                        it.id,
                        Time.valueOf(it.time),
                        it.user,
                        it.token
                    )
                )

                Unit
            }
        }
        return list.sortedBy {it.time}
    }

    fun translateUser(loginApi: LoginApi): User? {
        val userApi = loginApi.data ?: return null
        return User(userApi.id,  userApi.firstname, userApi.lastname, userApi.email)
    }
}