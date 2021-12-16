package com.xeflo.blessure

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.xeflo.blessure.databases.BloodPressureStorage
import com.xeflo.blessure.model.DownloadApi
import com.xeflo.blessure.model.TranslateApi
import com.squareup.moshi.Moshi
import com.xeflo.blessure.apimodel.AlarmsApi
import com.xeflo.blessure.apimodel.BloodPressuresApi
import com.xeflo.blessure.apimodel.LoginApi
import com.xeflo.blessure.datamodels.Alarm
import com.xeflo.blessure.datamodels.BloodPressure
import com.xeflo.blessure.datamodels.User
import com.xeflo.blessure.model.UploadApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.sql.Time
import java.time.LocalDateTime

class BloodPressureService: Service() {
    companion object {
        const val ACTION_DOWNLOAD_READY =
            "com.xeflo.blessure.DOWNLOAD_READY"
        const val ACTION_ALARM_DOWNLOAD_READY =
            "com.xeflo.blessure.ALARM_DOWNLOAD_READY"
    }

    private val downloadApi: DownloadApi by inject()
    private val translateApi: TranslateApi by inject()
    private val storage: BloodPressureStorage by inject()
    private val uploadApi: UploadApi by inject()
    private val moshi: Moshi by inject()

    private var user: User? = null
    private var firebaseToken: String? = null

    override fun onBind(intent: Intent?): IBinder? = Binder()

    fun fetchBloodPressures() {
        if (user == null) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val apiBloodPressures: BloodPressuresApi? = downloadApi.getBloodPressures(user!!.id).let {
                Log.e("DEBUGFH22: ", "received - " + it);
                moshi.adapter(BloodPressuresApi::class.java).fromJson(it)
            }
            if ( apiBloodPressures != null ) {
                val bloodPressures: List<BloodPressure> = translateApi.translateBloodPressures(apiBloodPressures)

                storage.putAllBloodPressures(bloodPressures)

                Intent(ACTION_DOWNLOAD_READY).also { resultIntent ->
                    LocalBroadcastManager.getInstance(applicationContext)
                        .sendBroadcast(resultIntent)

                }
            }
        }
    }

    fun addBloodPressure(sys: Int, dia: Int, datetime: LocalDateTime) {
        if (user == null) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            uploadApi.addBloodPressure(BloodPressure(-1, user!!.id, sys, dia, datetime)).let {
                fetchBloodPressures()
            }
        }
    }

    fun removeBloodPressure(bloodPressure: BloodPressure) {
        CoroutineScope(Dispatchers.IO).launch {
            uploadApi.removeBloodPressure(bloodPressure).let {
                fetchBloodPressures()
            }
        }
    }

    fun login(username: String, password: String, cb: (Boolean) -> Unit ) {
        var failureResId: Int? = null
        CoroutineScope(Dispatchers.IO).launch {
            val apiLogin: LoginApi? = uploadApi.login(username, password).let {
                Log.e("DEBUGFH11: ", it.toString())
                moshi.adapter(LoginApi::class.java).fromJson(it.toString())
            }
            if ( apiLogin != null ) {
                user = translateApi.translateUser(apiLogin)
                if (user == null) {
                    failureResId = R.string.login_failed_invalid_user
                }

                if (apiLogin.success == 0) {
                    failureResId = R.string.login_failed
                }
            } else {
                failureResId = R.string.login_failed_server_failure
            }

            if (user != null) {
                storage.putUser(user!!)
            } else {
                storage.clearUser()
            }

            withContext(Dispatchers.Main) {
                if (failureResId != null) {
                    Toast.makeText(applicationContext, getText(failureResId!!), Toast.LENGTH_SHORT)
                        .show()
                }
                cb(user != null)
            }
        }
    }

    fun fetchAlarms() {
        if (user == null) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val apiAlarms: AlarmsApi? = downloadApi.getAlarms(user!!.id).let {
                Log.e("DEBUGFH22: ", "received - " + it);
                moshi.adapter(AlarmsApi::class.java).fromJson(it)
            }
            if ( apiAlarms != null ) {
                val alarms: List<Alarm> = translateApi.translateAlarms(apiAlarms)

                storage.putAllAlarms(alarms)

                updateAlarms()

                Intent(ACTION_ALARM_DOWNLOAD_READY).also { resultIntent ->
                    LocalBroadcastManager.getInstance(applicationContext)
                        .sendBroadcast(resultIntent)

                }
            }
        }
    }

    fun addAlarm(time: Time) {
        if (user == null) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            uploadApi.addAlarm(Alarm(-1, time, user!!.id, firebaseToken ?: "")).let {
                fetchAlarms()
            }
        }
    }

    fun removeAlarm(alarm: Alarm) {
        CoroutineScope(Dispatchers.IO).launch {
            uploadApi.removeAlarm(alarm).let {
                fetchAlarms()
            }
        }
    }

    fun updateAlarms() {
        Log.e("DEBUGFH99: ", "updateAlarms")
        if (storage.getAllAlarms().isEmpty()) {
            return;
        }

        Log.e("DEBUGFH99: ", "check alarms vs token '" + firebaseToken + "'")

        storage.getAllAlarms().forEach {
            if (firebaseToken != null && it.token != firebaseToken) {
                Log.e("DEBUGFH99: ", "not the same");
                it.token = firebaseToken!!
                CoroutineScope(Dispatchers.IO).launch {
                    uploadApi.updateAlarm(it).let {
                        fetchAlarms()
                    }
                }
            }
        }
    }

    fun logout() {
        storage.clearUser()
        user = null
    }

    fun userLoggedIn(): Boolean {
        if (user == null) {
            user = storage.getUser()
        }

        return user != null
    }

    fun setFirebaseToken(token: String?) {
        firebaseToken = token

        updateAlarms()
    }

    inner class Binder : android.os.Binder() {
        val service
            get() = this@BloodPressureService
    }
}