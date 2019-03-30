package com.unitbvcv.btcarremote

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class ConnectViewModel: ViewModel() {

    val pairedDevicesList: MutableLiveData<List<String>> = MutableLiveData()

    val discoveredDevicesList: MutableLiveData<List<String>> = MutableLiveData()


    var deviceNameToConnect: String = ""

    var refreshRate: String = ""
    var timeoutCount: String = ""
}