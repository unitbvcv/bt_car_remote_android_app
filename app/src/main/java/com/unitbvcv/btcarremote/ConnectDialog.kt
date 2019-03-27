package com.unitbvcv.btcarremote

import android.app.AlertDialog

class ConnectDialog(activity: MainActivity) {

    private val builder: AlertDialog.Builder = activity.let { AlertDialog.Builder(it) }.apply {
        setTitle("Select device to connect to:")
        setMessage("TODO")
    }

    fun show() {
        builder.create().show()
    }

}