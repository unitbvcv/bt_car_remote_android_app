package com.unitbvcv.btcarremote

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel

class BluetoothViewModel : ViewModel() {

    // TODO: create observers for the live data, either here or in another class

    val joystickObserver: Observer<Pair<Double, Double>> = Observer { joystickPair: Pair<Double, Double>? ->
        // TODO: send Bluetooth data
        // or move it to a bluetooth manager
    }

    val ledToggle: MutableLiveData<Boolean> = MutableLiveData<Boolean>().also {
        it.value = false
    }

    val musicToggle: MutableLiveData<Boolean> = MutableLiveData<Boolean>().also {
        it.value = false
    }

    val displayText: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = ""
    }

    val refreshRate: MutableLiveData<Int> = MutableLiveData<Int>().also {
        it.value = 50
    }

    val timeoutCount: MutableLiveData<Int> = MutableLiveData<Int>().also {
        it.value = 10
    }

}