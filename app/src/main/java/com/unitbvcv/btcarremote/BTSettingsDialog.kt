package com.unitbvcv.btcarremote

import android.app.AlertDialog

class BTSettingsDialog(activity: MainActivity) {

    private val builder: AlertDialog.Builder = activity.let { AlertDialog.Builder(it) }.apply {
        setTitle("Bluetooth Settings")
        setMessage("TODO")
    }

    fun show() {
        builder.create().show()
    }

}