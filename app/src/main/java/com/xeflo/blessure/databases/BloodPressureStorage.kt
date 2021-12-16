package com.xeflo.blessure.databases

import android.util.Log
import androidx.room.EmptyResultSetException
import com.xeflo.blessure.datamodels.Alarm
import com.xeflo.blessure.datamodels.BloodPressure
import com.xeflo.blessure.datamodels.Settings
import com.xeflo.blessure.datamodels.User
import com.xeflo.blessure.entitymodels.AlarmEntity
import com.xeflo.blessure.entitymodels.BloodPressureEntity
import com.xeflo.blessure.entitymodels.SettingsEntity
import com.xeflo.blessure.entitymodels.UserEntity


class BloodPressureStorage(
    private val bloodPressureDb: BloodPressureDatabase,
    private val userDb: UserDatabase,
    private val alarmDb: AlarmDatabase,
    private val settingsDb: SettingsDatabase
) {
    private val bloodPressureDao by lazy { bloodPressureDb.bloodPressureDao() }
    private val userDao by lazy { userDb.userDao() }
    private val alarmDao by lazy { alarmDb.alarmDao() }
    private val settingsDao by lazy { settingsDb.settingsDao() }


    fun putAllBloodPressures(bloodPressures: List<BloodPressure>) {
        bloodPressureDb.runInTransaction {
            bloodPressureDao.deleteAll()

            bloodPressures.forEach { bloodPressure: BloodPressure ->
                bloodPressureDao.insert(
                    BloodPressureEntity(
                        bloodPressure.id,
                        bloodPressure.userId,
                        bloodPressure.sys,
                        bloodPressure.dia,
                        bloodPressure.datetime
                    )
                )
            }
        }
    }

    fun getAllBloodPressures(): List<BloodPressure> {
        val retBloodPressures: MutableList<BloodPressure> = mutableListOf()
        bloodPressureDao.getList().let { bloodPressures ->
            bloodPressures.forEach { bloodPressure ->
                retBloodPressures.add(
                    BloodPressure(
                        bloodPressure.id,
                        bloodPressure.userId,
                        bloodPressure.sys,
                        bloodPressure.dia,
                        bloodPressure.datetime
                    )
                )
            }
        }

        return retBloodPressures
    }

    fun putAllAlarms(alarms: List<Alarm>) {
        alarmDb.runInTransaction {
            alarmDao.deleteAll()

            Log.e("DEBUGFH99: ", "nof alarms " + alarms.size);
            alarms.forEach { alarm: Alarm ->
                alarmDao.insert(
                    AlarmEntity(
                        alarm.id,
                        alarm.time,
                        alarm.user,
                        alarm.token
                    )
                )
            }
        }
    }

    fun getAllAlarms(): List<Alarm> {
        val retAlarms: MutableList<Alarm> = mutableListOf()
        alarmDao.getList().let { alarms ->
            alarms.forEach { alarm ->
                retAlarms.add(
                    Alarm(
                        alarm.id,
                        alarm.time,
                        alarm.user,
                        alarm.token
                    )
                )
            }
        }

        return retAlarms
    }

    fun removeAlarm(alarm: Alarm) {
        alarmDao.delete(alarmDao.getById(alarm.id))
    }

    fun removeBloodPressure(bloodPressure: BloodPressure) {
        bloodPressureDao.delete(bloodPressureDao.getById(bloodPressure.id))
    }

    fun getUser(): User? {
        var retUser: User
        Log.e("DEBUGFH33: ", "test123")
        if (userDao.getList().isEmpty()) {
            return null
        }
        userDao.getList()[0].let { user ->
            retUser = User(
                user.id,
                user.firstname,
                user.lastname,
                user.email
            )
        }
        return retUser
    }

    fun putUser(user: User) {
        userDb.runInTransaction {
            userDao.deleteAll()

            userDao.insert(
                UserEntity(
                    user.id,
                    user.firstname,
                    user.lastname,
                    user.email
                )
            )
        }
    }

    fun clearUser() {
        userDb.runInTransaction {
            userDao.deleteAll()
        }
    }

    fun getSettings(): Settings? {
        val user = getUser() ?: return null

        var settings: Settings?;
        try {
            settingsDao.getById(user.id).let {
                settings = if (it == null) {
                    null
                } else {
                    Settings(it.id, it.dashboardDays)
                }
            }
        } catch(e: EmptyResultSetException) {
            settings = null
        }

        return settings
    }

    fun insertSettings(settings: Settings) {
        val user = getUser() ?: return

        settingsDao.insert(SettingsEntity(user.id, settings.dashboardDays))
    }

    fun updateSettings(settings: Settings) {
        val settingsEntity = settingsDao.getById(settings.id)
        settingsEntity.dashboardDays = settings.dashboardDays
        Log.e("DEBUGFH86: ", "update to " + settingsEntity.dashboardDays)
        settingsDao.update(settingsEntity)
    }
}