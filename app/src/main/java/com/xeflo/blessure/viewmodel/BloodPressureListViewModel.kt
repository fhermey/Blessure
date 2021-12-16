package com.xeflo.blessure.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.*
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.xeflo.blessure.BloodPressureService
import com.xeflo.blessure.databases.BloodPressureStorage
import com.xeflo.blessure.datamodels.BloodPressure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BloodPressureListViewModel(app: Application,
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
                refreshBloodPressures()
            }

            override fun onServiceDisconnected(name: ComponentName?) = Unit
        }
    }

    private val loadListener by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) =
                loadBloodPressures(_bloodPressureList)
        }
    }

    private val _bloodPressureList by lazy {
        MutableLiveData<List<BloodPressure>>().also {
            loadBloodPressures(it);
        }
    }

    val bloodPressureList: LiveData<List<BloodPressure>>
        get() = _bloodPressureList

    init {
        LocalBroadcastManager.getInstance(app).registerReceiver(
            loadListener, IntentFilter(BloodPressureService.ACTION_DOWNLOAD_READY)
        )

        app.bindService(
            Intent(app, BloodPressureService::class.java), serviceConnection, Context.BIND_AUTO_CREATE
        )
    }

    fun refreshBloodPressures() {
        bloodPressureService?.fetchBloodPressures()
    }

    fun removeBloodPressure(bloodPressure: BloodPressure) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.e("DEBUGFH55: ", "SYS: " + bloodPressure.sys + ", DIA: " + bloodPressure.dia + ", ID: " + bloodPressure.id)
                bloodPressureStorage.removeBloodPressure(bloodPressure)
                bloodPressureService?.removeBloodPressure(bloodPressure)
            }
        }

        val newList = _bloodPressureList.value?.toMutableList()
        newList?.remove(bloodPressure)
        _bloodPressureList.value = newList
    }

    private fun loadBloodPressures(list: MutableLiveData<List<BloodPressure>>) {
        viewModelScope.launch {
            list.value = withContext(Dispatchers.IO) {
                bloodPressureStorage.getAllBloodPressures().sortedByDescending { it.datetime }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(loadListener)
        getApplication<Application>().unbindService(serviceConnection)
    }
}