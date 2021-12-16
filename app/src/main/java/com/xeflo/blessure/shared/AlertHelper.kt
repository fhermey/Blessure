package com.xeflo.blessure.shared

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.xeflo.blessure.R

class AlertHelper(private val context: Context) {
    fun confirmAction(title: String,
                      message: String,
                      positiveListener: (dialog: DialogInterface, which: Int) -> Unit,
                      negativeListener: (dialog: DialogInterface, which: Int) -> Unit = { _, _ -> Unit }) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.yes_button, positiveListener)
            setNegativeButton(R.string.no_button, negativeListener)
        }.let {
            it.create().show()
        }
    }
}