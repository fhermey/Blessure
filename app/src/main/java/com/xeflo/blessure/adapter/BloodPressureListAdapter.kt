package com.xeflo.blessure.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xeflo.blessure.callbacks.BloodPressureListDiffCallback
import com.xeflo.blessure.databinding.ItemBloodPressureBinding
import com.xeflo.blessure.datamodels.BloodPressure
import com.xeflo.blessure.shared.BloodPressureHelper
import com.xeflo.blessure.viewmodel.BloodPressureListViewModel

class BloodPressureListAdapter(val bloodPressures: MutableList<BloodPressure>, private val bloodPressureHelper: BloodPressureHelper, private val viewModel: BloodPressureListViewModel): RecyclerView.Adapter<BloodPressureListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BloodPressureListViewHolder(
            parent.context,
            bloodPressureHelper,
            ItemBloodPressureBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).apply {
                viewModel = this@BloodPressureListAdapter.viewModel
            }
        )

    override fun getItemCount() = bloodPressures.size ?: 0

    override fun onBindViewHolder(holder: BloodPressureListViewHolder, idx: Int) {
        holder.bind(bloodPressures.get(idx))
    }

    fun setData(data: List<BloodPressure>) {
        val diffCallback = BloodPressureListDiffCallback(bloodPressures, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        bloodPressures.clear()
        bloodPressures.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }
}
