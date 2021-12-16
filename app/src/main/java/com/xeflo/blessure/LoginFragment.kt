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
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.xeflo.blessure.databinding.FragmentBloodPressureAddBinding
import com.xeflo.blessure.databinding.FragmentLoginBinding
import com.xeflo.blessure.datamodels.BloodPressure
import org.koin.android.ext.android.inject
import java.time.LocalDateTime
import java.util.*
import androidx.core.content.ContextCompat.getSystemService





class LoginFragment : Fragment() {

    private val appContext: Context by inject()

    private var _binding: FragmentLoginBinding? = null

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
        (requireActivity() as BlessureActivity).hideNavigation()
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.login.setOnTouchListener { v, motionEvent ->
            if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                val inputManager: InputMethodManager? =
                    getSystemService(requireContext(), InputMethodManager::class.java)

                inputManager?.hideSoftInputFromWindow(
                    v.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )

                bloodPressureService!!.login(
                    binding.user.text.toString(),
                    binding.password.text.toString()
                ) {
                    if (it) {
                        Log.e("DEBUGFH99: ", "result - " + it)
                        findNavController().navigateUp()
                    }
                }
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
