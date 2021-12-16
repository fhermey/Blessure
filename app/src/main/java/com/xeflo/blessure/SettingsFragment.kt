package com.xeflo.blessure

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.xeflo.blessure.shared.BloodPressureHelper
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.koin.android.ext.android.inject

class SettingsFragment : Fragment() {
    val bloodPressureHelper: BloodPressureHelper by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false).apply {
        setHasOptionsMenu(true)

        dashboardDays.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, fromUser: Boolean) {
                // Display the current progress of SeekBar
                dashboardDaysCount.text = "$i"
                Log.e("DEBUGFH86: ", "onProgressChanged - " + i)
                if (fromUser) {
                    bloodPressureHelper.setDashboardDays(i)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                // do nothing
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                // do nothing
            }
        })

        dashboardDays.max = bloodPressureHelper.getMaxDashboardDays()
        dashboardDays.progress = bloodPressureHelper.getDashboardDays()
        dashboardDaysCount.text = bloodPressureHelper.getDashboardDays().toString()
    }
}