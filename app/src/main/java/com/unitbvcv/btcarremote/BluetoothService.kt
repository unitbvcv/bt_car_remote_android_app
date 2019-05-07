package com.unitbvcv.btcarremote

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BluetoothService : Service() {
    private val binder = BluetoothServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun openConnection() {
        Log.d("service", "opened connection")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getCurrentTime(): String {
        return "4"
    }

    inner class BluetoothServiceBinder: Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }
}
