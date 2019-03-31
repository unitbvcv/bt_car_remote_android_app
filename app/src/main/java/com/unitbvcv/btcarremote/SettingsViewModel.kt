package com.unitbvcv.btcarremote

import android.arch.lifecycle.ViewModel
import android.bluetooth.BluetoothDevice

class SettingsViewModel: ViewModel() {
    var refreshRate: String = "50"
    var timeoutCount: String = "10"
    var deviceToConnectTo: BluetoothDevice? = null
}
