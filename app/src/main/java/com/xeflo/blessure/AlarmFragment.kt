package com.xeflo.blessure

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.xeflo.blessure.adapter.AlarmListAdapter
import com.xeflo.blessure.adapter.BloodPressureListAdapter
import com.xeflo.blessure.callbacks.SwipeToDeleteAlarmCallback
import com.xeflo.blessure.callbacks.SwipeToDeleteCallback
import com.xeflo.blessure.datamodels.BloodPressure
import com.xeflo.blessure.shared.AlertHelper
import com.xeflo.blessure.shared.BloodPressureHelper
import com.xeflo.blessure.viewmodel.AlarmListViewModel
import com.xeflo.blessure.viewmodel.BloodPressureListViewModel
import kotlinx.android.synthetic.main.fragment_alarm_list.*
import kotlinx.android.synthetic.main.fragment_alarm_list.view.*
import kotlinx.android.synthetic.main.fragment_blood_pressure_list.*
import kotlinx.android.synthetic.main.fragment_blood_pressure_list.fab
import kotlinx.android.synthetic.main.fragment_blood_pressure_list.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class AlarmFragment : Fragment() {
    private val appContext: Context by inject()

    private val viewModel: AlarmListViewModel by sharedViewModel()

    private var swipeContainer: SwipeRefreshLayout? = null

    private var initialLoad = true

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_alarm_list, container, false).apply {
        setHasOptionsMenu(true)

        swipeContainer = (this.alarmListSwipeRefreshLayout).apply {
            this.setOnRefreshListener {
                updateBloodPressures()
            }
        }

        this.alarmListRecyclerView.apply recyclerView@ {
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                this@recyclerView.addItemDecoration(this)
            }

            val alarmListAdapter = AlarmListAdapter(mutableListOf(), viewModel)
            adapter = alarmListAdapter
            layoutManager = LinearLayoutManager(context)

            // swipe delete handler
            val swipeHandler = object : SwipeToDeleteAlarmCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val alarm = viewModel.alarmList.value?.get(viewHolder.bindingAdapterPosition)
                    if (alarm != null) {
                        AlertHelper(context).confirmAction("Remove reminder",
                            "Are you sure you want to remove this reminder?",
                            { _, _ ->
                                viewModel.removeAlarm(alarm)
                            },
                            { _, _ ->
                                adapter?.notifyItemChanged(viewHolder.adapterPosition)
                            }
                        )
                    }
                }
            }

            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(this@recyclerView)

            // observe for new recipes
            viewModel.alarmList.observe(viewLifecycleOwner, Observer {
                if ( initialLoad ) {
                    initialLoad = false
                } else {
                    swipeContainer?.isRefreshing = false
                }
                alarmListAdapter.setData(it)
            })

            // initial recipe loading
            if ( viewModel.alarmList.value == null || (viewModel.alarmList.value as List<BloodPressure>).isEmpty() ) {
                swipeContainer?.isRefreshing = true
            }

            updateBloodPressures()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.add.setOnClickListener { view ->
            findNavController().navigate(R.id.aciton_AlarmFragment_to_AlarmAddFragment)
        }
    }

    private fun updateBloodPressures() {
        viewModel.refreshAlarms()
    }
}