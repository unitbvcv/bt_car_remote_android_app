package com.unitbvcv.btcarremote

import android.os.Binder

class BluetoothServiceBinder(val service: BluetoothService) : Binder()