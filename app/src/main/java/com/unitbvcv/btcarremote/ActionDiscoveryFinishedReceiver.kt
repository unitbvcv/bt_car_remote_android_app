package com.unitbvcv.btcarremote

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.Menu

class ActionDiscoveryFinishedReceiver : BroadcastReceiver() {

    var menu: Menu? = null

    override fun onReceive(context: Context, intent: Intent) {
        val action: String = intent.action
        when(action) {
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                menu?.findItem(R.id.refreshDiscoveryItem)?.isVisible = true
                menu?.findItem(R.id.loadingDiscoveryItem)?.isVisible = false
                menu?.findItem(R.id.stopDiscoveryItem)?.isVisible = false
            }
        }
    }

}