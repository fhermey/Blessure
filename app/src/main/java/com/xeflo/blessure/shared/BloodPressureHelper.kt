package com.xeflo.blessure.shared

import android.util.Log
import com.xeflo.blessure.R
import com.xeflo.blessure.databases.BloodPressureStorage
import com.xeflo.blessure.datamodels.BloodPressure
import com.xeflo.blessure.datamodels.Settings
import java.lang.Integer.max
import java.lang.Integer.min

class BloodPressureHelper(private val storage: BloodPressureStorage) {
    private val defaultSettings = Settings(-1, 7)

    fun getAverageSys(): Int {
        val bloodPressures = getBloodPressures()
        val sum = bloodPressures.sumOf {it.sys}
        if (sum == 0) {
            return 0
        }
        return sum / bloodPressures.size
    }

    fun getAverageDia(): Int {
        val bloodPressures = getBloodPressures()
        val sum = bloodPressures.sumOf {it.dia}
        if (sum == 0) {
            return 0
        }
        return sum / bloodPressures.size
    }

    fun getMinimumSys(): Int {
        val bloodPressures = getBloodPressures()
        if (bloodPressures.isEmpty()) {
            return 0
        }
        return bloodPressures.minOf {it.sys}
    }

    fun getMinimumDia(): Int {
        val bloodPressures = getBloodPressures()
        if (bloodPressures.isEmpty()) {
            return 0
        }
        return bloodPressures.minOf {it.dia}
    }

    fun getMaximumSys(): Int {
        val bloodPressures = getBloodPressures()
        if (bloodPressures.isEmpty()) {
            return 0
        }
        return bloodPressures.maxOf {it.sys}
    }

    fun getMaximumDia(): Int {
        val bloodPressures = getBloodPressures()
        if (bloodPressures.isEmpty()) {
            return 0
        }
        return bloodPressures.maxOf {it.dia}
    }

    fun getIndicatorColorFor(sys: Int, dia: Int): Int {
        return if (sys < 120 && dia < 80) {
            R.color.grade_0
        } else if (sys < 129 && dia < 84) {
            R.color.grade_1
        } else if (sys < 139 && dia < 89) {
            R.color.grade_2
        } else if (sys < 159 && dia < 99) {
            R.color.grade_3
        } else if (sys < 179 && dia < 109) {
            R.color.grade_4
        } else {
            R.color.grade_5
        }
    }

    fun setDashboardDays(dashboardDays: Int) {
        val settings = getSettings()
        settings.dashboardDays = dashboardDays
        storage.updateSettings(settings)
    }

    fun getDashboardDays(): Int {
        return getSettings().dashboardDays
    }

    fun getMaxDashboardDays(): Int {
        return min(storage.getAllBloodPressures().size, 30)
    }

    private fun getSettings(): Settings {
        val settings = storage.getSettings()
        Log.e("DEBUGFH86: ", "get " + settings)
        if (settings == null) {
            storage.insertSettings(defaultSettings)
            return defaultSettings
        }

        return settings
    }

    private fun getBloodPressures(): List<BloodPressure> {
        return storage.getAllBloodPressures().sortedByDescending { it.datetime }.subList(0, getDashboardDays())
    }
}