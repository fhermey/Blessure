package com.xeflo.blessure.callbacks

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.xeflo.blessure.datamodels.BloodPressure

class BloodPressureListDiffCallback(private val oldList: List<BloodPressure>, private val newList: List<BloodPressure>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (id) = oldList[oldItemPosition]
        val (id1) = newList[newItemPosition]

        return id == id1
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldList[oldPosition] == newList[newPosition]
    }

    @Nullable
    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
        return super.getChangePayload(oldPosition, newPosition)
    }
}