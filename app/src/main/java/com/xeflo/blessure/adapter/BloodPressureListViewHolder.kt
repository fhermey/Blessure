package com.xeflo.blessure.adapter

import android.content.Context
import android.text.Html
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.xeflo.blessure.R
import com.xeflo.blessure.databinding.ItemBloodPressureBinding
import com.xeflo.blessure.datamodels.BloodPressure
import com.xeflo.blessure.shared.BloodPressureHelper
import java.time.format.DateTimeFormatter

class BloodPressureListViewHolder(val context: Context, private val bloodPressureHelper: BloodPressureHelper, private val binding: ItemBloodPressureBinding): RecyclerView.ViewHolder(binding.root) {
   fun bind(bloodPressure: BloodPressure) {
        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

        Log.e("DEBUGFH40: ", bloodPressure.datetime.toString());

        binding.sys.text = Html.fromHtml(context.getString(R.string.sys, bloodPressure.sys), Html.FROM_HTML_MODE_LEGACY)
        binding.dia.text = Html.fromHtml(context.getString(R.string.dia, bloodPressure.dia), Html.FROM_HTML_MODE_LEGACY)
        binding.datetime.text = bloodPressure.datetime.format(formatter)

        binding.card.setCardBackgroundColor(context.getColor(bloodPressureHelper.getIndicatorColorFor(bloodPressure.sys, bloodPressure.dia)))
    }
}