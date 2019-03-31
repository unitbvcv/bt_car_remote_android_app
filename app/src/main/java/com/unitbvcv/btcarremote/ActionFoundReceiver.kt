package com.unitbvcv.btcarremote

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ActionFoundReceiver(private val viewModel: ConnectViewModel) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action: String = intent.action
        when(action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val list = viewModel.discoveredDevicesList.value?.toMutableList() ?: mutableListOf<String>()
                list += "${device.name} ${device.address}"
                viewModel.discoveredDevicesList.value = list
            }
        }
    }

}