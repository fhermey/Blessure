package com.xeflo.blessure.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.xeflo.blessure.databinding.ItemAlarmBinding
import com.xeflo.blessure.datamodels.Alarm

class AlarmListViewHolder(val context: Context, private val binding: ItemAlarmBinding): RecyclerView.ViewHolder(binding.root) {
   fun bind(alarm: Alarm) {
       val parts = alarm.time.toString().split(":")
       (parts[0] + ":" + parts[1]).also { binding.time.text = it }

       //var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

        /*binding.sys.text = Html.fromHtml(context.getString(R.string.sys, bloodPressure.sys), Html.FROM_HTML_MODE_LEGACY)
        binding.dia.text = Html.fromHtml(context.getString(R.string.dia, bloodPressure.dia), Html.FROM_HTML_MODE_LEGACY)
        binding.datetime.text = bloodPressure.datetime.format(formatter)

        binding.card.setCardBackgroundColor(context.getColor(bloodPressureHelper.getIndicatorColorFor(bloodPressure.sys, bloodPressure.dia)))*/
    }
}