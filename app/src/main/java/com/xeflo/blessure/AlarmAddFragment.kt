package com.xeflo.blessure

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import androidx.navigation.fragment.findNavController
import com.xeflo.blessure.databinding.FragmentAlarmAddBinding
import kotlinx.android.synthetic.main.fragment_alarm_add.*
import org.koin.android.ext.android.inject
import java.sql.Time
import java.time.LocalTime


class AlarmAddFragment : Fragment() {

    private val appContext: Context by inject()

    private var _binding: FragmentAlarmAddBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var bloodPressureService: BloodPressureService? = null
    private val serviceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.e("DEBUGFH22: ", "onServiceConnected");
                bloodPressureService = (service as? BloodPressureService.Binder)?.service ?: run {
                    appContext.unbindService(this)
                    return
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) = Unit
        }
    }

    init {
        appContext.bindService(
            Intent(appContext, BloodPressureService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.time.setIs24HourView(true)

        binding.cancel.setOnTouchListener { v, motionEvent ->
            if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                findNavController().navigateUp()
            }
            true
        }
        binding.save.setOnTouchListener { v, motionEvent ->
            if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                val sqlTime1 = Time.valueOf("18:45:20")
                Log.e("DEBUGFH43: ", "SqlTime1: $sqlTime1")
                Log.e("DEBUGFH43: ", LocalTime.of(this.time.hour, this.time.minute).toString() + ":00")
                bloodPressureService!!.addAlarm(
                    Time.valueOf(LocalTime.of(this.time.hour, this.time.minute, 0).toString() + ":00")
                )
                findNavController().navigateUp()
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        appContext.unbindService(serviceConnection)
    }
}
