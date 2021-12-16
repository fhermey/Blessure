package com.xeflo.blessure.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xeflo.blessure.callbacks.AlarmListDiffCallback
import com.xeflo.blessure.databinding.ItemAlarmBinding
import com.xeflo.blessure.datamodels.Alarm
import com.xeflo.blessure.viewmodel.AlarmListViewModel

class AlarmListAdapter(val alarms: MutableList<Alarm>, private val viewModel: AlarmListViewModel): RecyclerView.Adapter<AlarmListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AlarmListViewHolder(
            parent.context,
            ItemAlarmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).apply {
                viewModel = this@AlarmListAdapter.viewModel
            }
        )

    override fun getItemCount() = alarms.size ?: 0

    override fun onBindViewHolder(holder: AlarmListViewHolder, idx: Int) {
        holder.bind(alarms.get(idx))
    }

    fun setData(data: List<Alarm>) {
        val diffCallback = AlarmListDiffCallback(alarms, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        alarms.clear()
        alarms.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }
}
