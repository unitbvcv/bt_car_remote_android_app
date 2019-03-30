package com.unitbvcv.btcarremote

import android.arch.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {
    var refreshRate: String = "50"
    var timeoutCount: String = "10"
}
