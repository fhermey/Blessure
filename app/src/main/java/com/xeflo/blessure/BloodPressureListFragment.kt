package com.xeflo.blessure

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.xeflo.blessure.adapter.BloodPressureListAdapter
import com.xeflo.blessure.callbacks.SwipeToDeleteCallback
import com.xeflo.blessure.datamodels.BloodPressure
import com.xeflo.blessure.shared.AlertHelper
import com.xeflo.blessure.shared.BloodPressureHelper
import com.xeflo.blessure.viewmodel.BloodPressureListViewModel
import kotlinx.android.synthetic.main.fragment_blood_pressure_list.*
import kotlinx.android.synthetic.main.fragment_blood_pressure_list.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class BloodPressureListFragment : Fragment() {

    private val bloodPressureHelper: BloodPressureHelper by inject()

    private val viewModel: BloodPressureListViewModel by sharedViewModel()

    private var swipeContainer: SwipeRefreshLayout? = null

    private var initialLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_blood_pressure_list, container, false).apply {
        setHasOptionsMenu(true)

        swipeContainer = (this.bloodPressureListSwipeRefreshLayout).apply {
            this.setOnRefreshListener {
                updateBloodPressures()
            }
        }

        this.bloodPressureListRecyclerView.apply recyclerView@ {
            val recipeListAdapter = BloodPressureListAdapter(mutableListOf(), bloodPressureHelper, viewModel)
            adapter = recipeListAdapter
            layoutManager = LinearLayoutManager(context)

            // swipe delete handler
            val swipeHandler = object : SwipeToDeleteCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val bloodPressure = viewModel.bloodPressureList.value?.get(viewHolder.bindingAdapterPosition)
                    if (bloodPressure != null) {
                        AlertHelper(context).confirmAction("Delete entry",
                            "Are you sure you want to delete this entry?",
                            { _, _ ->
                                viewModel.removeBloodPressure(bloodPressure)
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
            viewModel.bloodPressureList.observe(viewLifecycleOwner, Observer {
                if ( initialLoad ) {
                    initialLoad = false
                } else {
                    swipeContainer?.isRefreshing = false
                }
                recipeListAdapter.setData(it)
            })

            // initial recipe loading
            if ( viewModel.bloodPressureList.value == null || (viewModel.bloodPressureList.value as List<BloodPressure>).isEmpty() ) {
                swipeContainer?.isRefreshing = true
            }

            updateBloodPressures()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.fab.setOnClickListener { view ->
            findNavController().navigate(R.id.action_ListFragment_to_AddFragment)
        }
    }

    private fun updateBloodPressures() {
        viewModel.refreshBloodPressures()
    }
}