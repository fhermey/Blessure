package com.xeflo.blessure

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.xeflo.blessure.databinding.FragmentBloodPressureAddBinding
import com.xeflo.blessure.datamodels.BloodPressure
import org.koin.android.ext.android.inject
import java.time.LocalDateTime
import java.util.*


class BloodPressureAddFragment : Fragment() {

    private val appContext: Context by inject()

    private var _binding: FragmentBloodPressureAddBinding? = null

    private var mDatePickerDialog: DatePickerDialog? = null
    private var mTimePickerDialog: TimePickerDialog? = null

    private var mYear: Int = 2021
    private var mMonth: Int = 1
    private var mDay: Int = 1
    private var mHour: Int = 0
    private var mMinute: Int = 0

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

        _binding = FragmentBloodPressureAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        mDay = Calendar.getInstance().get(Calendar.DATE);

        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);

        updateDateTime()

        Log.e("DEBUGFH22: ", "here");

        binding.cardDatetime.setOnTouchListener { v, motionEvent ->
            if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                Log.e("DEBUGFH22: ", "mhhhh");
                showDatePicker()
            }
            true
        }

        binding.cancel.setOnTouchListener { v, motionEvent ->
            if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                findNavController().navigateUp()
            }
            true
        }
        binding.save.setOnTouchListener { v, motionEvent ->
            if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                val inputManager: InputMethodManager? =
                    ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)

                inputManager?.hideSoftInputFromWindow(
                    v.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )

                val cal = Calendar.getInstance()
                cal.set(mYear, mMonth, mDay, mHour, mMinute)
                bloodPressureService!!.addBloodPressure(
                    binding.systolic.text.toString().toInt(),
                    binding.diastolic.text.toString().toInt(),
                    LocalDateTime.of(mYear, mMonth, mDay, mHour,mMinute)
                )
                findNavController().navigateUp()
            }
            true
        }
    }

    private fun showDatePicker() {
        mDatePickerDialog = DatePickerDialog(
            requireContext(),
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                mDay = dayOfMonth
                mMonth = monthOfYear + 1
                mYear = year

                updateDateTime()

                showTimePicker()
            },
            mYear,
            mMonth - 1,
            mDay
        )

        mDatePickerDialog?.show()
    }

    private fun showTimePicker() {
        mTimePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute

                updateDateTime()
                mTimePickerDialog?.dismiss()
            },
            mHour,
            mMinute,
            true
        )

        mTimePickerDialog?.show()
        mDatePickerDialog?.dismiss()
    }

    private fun updateDateTime() {
        binding.datetime.setText(
            resources.getString(
                R.string.datetime,
                String.format("%02d", mDay),
                String.format("%02d", mMonth),
                mYear,
                String.format("%02d", mHour),
                String.format("%02d", mMinute)
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        appContext.unbindService(serviceConnection)
    }
}
