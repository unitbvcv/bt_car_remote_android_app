package com.unitbvcv.btcarremote

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.bluetooth.BluetoothDevice

class ConnectViewModel: ViewModel() {

    val pairedDevicesList: MutableLiveData<List<String>> = MutableLiveData()

    val discoveredDevicesList: MutableLiveData<List<String>> = MutableLiveData()


    var deviceToConnectTo: BluetoothDevice? = null

    val bluetoothDevicesMap = mutableMapOf<String, BluetoothDevice>()

    var refreshRate: String = ""
    var timeoutCount: String = ""
}