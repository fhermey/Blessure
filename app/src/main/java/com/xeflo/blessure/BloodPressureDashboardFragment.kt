package com.xeflo.blessure

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.xeflo.blessure.adapter.BloodPressureListAdapter
import com.xeflo.blessure.databinding.FragmentBloodPressureDashboardBinding
import com.xeflo.blessure.datamodels.BloodPressure
import com.xeflo.blessure.shared.BloodPressureHelper
import com.xeflo.blessure.viewmodel.BloodPressureListViewModel
import kotlinx.android.synthetic.main.fragment_blood_pressure_dashboard.*
import kotlinx.android.synthetic.main.fragment_blood_pressure_list.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.Integer.min

class BloodPressureDashboardFragment : Fragment() {
    private val appContext: Context by inject()

    private val viewModel: BloodPressureListViewModel by sharedViewModel()

    private val bloodPressureHelper: BloodPressureHelper by inject()

    private var _binding: FragmentBloodPressureDashboardBinding? = null

    private var swipeContainer: SwipeRefreshLayout? = null

    private var initialLoad = true

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

                if (!bloodPressureService!!.userLoggedIn()) {
                    findNavController().navigate(R.id.action_DashboardFragment_to_LoginFragment)
                }

                bloodPressureService!!.fetchAlarms()
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
        _binding = FragmentBloodPressureDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as BlessureActivity).showNavigation()

        initList()
        if (bloodPressureService != null) {
            bloodPressureService!!.fetchAlarms()
        }

        this.fab.setOnClickListener { view ->
            findNavController().navigate(R.id.action_DashboardFragment_to_AddFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateOverview() {
        Log.e("DEBUGFH13: ", "initOverview");
        Log.e("DEBUGFH13: ", "avg_sys: " + bloodPressureHelper.getAverageSys())
        Log.e("DEBUGFH13: ", "avg_dia: " + bloodPressureHelper.getAverageDia())
        Log.e("DEBUGFH13: ", "min_sys: " + bloodPressureHelper.getMinimumSys())
        Log.e("DEBUGFH13: ", "min_dia: " + bloodPressureHelper.getMinimumDia())
        Log.e("DEBUGFH13: ", "max_sys: " + bloodPressureHelper.getMaximumSys())
        Log.e("DEBUGFH13: ", "max_dia: " + bloodPressureHelper.getMaximumDia())

        this.sys_avg.text = bloodPressureHelper.getAverageSys().toString()
        this.dia_avg.text = bloodPressureHelper.getAverageDia().toString()
        this.sys_min.text = bloodPressureHelper.getMinimumSys().toString()
        this.dia_min.text = bloodPressureHelper.getMinimumDia().toString()
        this.sys_max.text = bloodPressureHelper.getMaximumSys().toString()
        this.dia_max.text = bloodPressureHelper.getMaximumDia().toString()

        this.avg_indicator.backgroundTintList = ColorStateList.valueOf(resources.getColor(bloodPressureHelper.getIndicatorColorFor(bloodPressureHelper.getAverageSys(), bloodPressureHelper.getAverageDia()), context?.theme))
        this.min_indicator.backgroundTintList = ColorStateList.valueOf(resources.getColor(bloodPressureHelper.getIndicatorColorFor(bloodPressureHelper.getMinimumSys(), bloodPressureHelper.getMinimumDia()), context?.theme))
        this.max_indicator.backgroundTintList = ColorStateList.valueOf(resources.getColor(bloodPressureHelper.getIndicatorColorFor(bloodPressureHelper.getMaximumSys(), bloodPressureHelper.getMaximumDia()), context?.theme))
    }

    private fun initList() {
        swipeContainer = (this.bloodPressureListSwipeRefreshLayout).apply {
            this.setOnRefreshListener {
                updateBloodPressures()
            }
        }

        this.bloodPressureListRecyclerView.apply recyclerView@ {
            val recipeListAdapter = BloodPressureListAdapter(mutableListOf(), bloodPressureHelper, viewModel)
            adapter = recipeListAdapter
            layoutManager = LinearLayoutManager(context)

            // observe for new recipes
            viewModel.bloodPressureList.observe(viewLifecycleOwner, Observer {
                if ( initialLoad ) {
                    initialLoad = false
                } else {
                    swipeContainer?.isRefreshing = false
                }
                recipeListAdapter.setData(it.subList(0, min(it.size, bloodPressureHelper.getDashboardDays())))
                (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
                updateOverview()
            })

            // initial recipe loading
            if ( viewModel.bloodPressureList.value == null || (viewModel.bloodPressureList.value as List<BloodPressure>).isEmpty() ) {
                bloodPressureListSwipeRefreshLayout?.isRefreshing = true
            }

            updateBloodPressures()
        }
    }

    private fun updateBloodPressures() {
        viewModel.refreshBloodPressures()
    }
}