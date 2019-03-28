package com.unitbvcv.btcarremote

import android.app.Activity
import android.app.AlertDialog

class AboutDialog(activity: Activity) {

    private val builder: AlertDialog.Builder = activity.let { AlertDialog.Builder(it) }.apply {
        setTitle("About BT Car Controller")
        setMessage(
            "App created by Cosmin Polifronie and Vlad Vrabie \n\n" +
            "UnitBv 2019"
        )
    }


    fun show() {
        builder.create().show()
    }

}