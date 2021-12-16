package com.xeflo.blessure.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.*
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.xeflo.blessure.BloodPressureService
import com.xeflo.blessure.databases.BloodPressureStorage
import com.xeflo.blessure.datamodels.Alarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmListViewModel(app: Application,
                         private val bloodPressureStorage: BloodPressureStorage) : AndroidViewModel(app) {
    @SuppressLint("StaticFieldLeak")
    private var bloodPressureService: BloodPressureService? = null
    private val serviceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                bloodPressureService = (service as? BloodPressureService.Binder)?.service ?: run {
                    app.unbindService(this)
                    return
                }
                refreshAlarms()
            }

            override fun onServiceDisconnected(name: ComponentName?) = Unit
        }
    }

    private val loadListener by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) =
                loadAlarms(_alarmList)
        }
    }

    private val _alarmList by lazy {
        MutableLiveData<List<Alarm>>().also {
            loadAlarms(it);
        }
    }

    val alarmList: LiveData<List<Alarm>>
        get() = _alarmList

    init {
        LocalBroadcastManager.getInstance(app).registerReceiver(
            loadListener, IntentFilter(BloodPressureService.ACTION_ALARM_DOWNLOAD_READY)
        )

        app.bindService(
            Intent(app, BloodPressureService::class.java), serviceConnection, Context.BIND_AUTO_CREATE
        )
    }

    fun refreshAlarms() {
        bloodPressureService?.fetchAlarms()
    }

    fun removeAlarm(alarm: Alarm) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                bloodPressureStorage.removeAlarm(alarm)
                bloodPressureService?.removeAlarm(alarm)
            }
        }

        val newList = _alarmList.value?.toMutableList()
        newList?.remove(alarm)
        _alarmList.value = newList
    }

    private fun loadAlarms(list: MutableLiveData<List<Alarm>>) {
        viewModelScope.launch {
            list.value = withContext(Dispatchers.IO) {
                bloodPressureStorage.getAllAlarms().sortedBy { it.time }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(loadListener)
        getApplication<Application>().unbindService(serviceConnection)
    }
}