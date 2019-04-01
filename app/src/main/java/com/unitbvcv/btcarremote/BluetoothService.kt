package com.unitbvcv.btcarremote

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BluetoothService : Service() {
    private val binder = BluetoothServiceBinder(this)

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun openConnection() {
        Log.d("service", "opened connection")
    }
}
