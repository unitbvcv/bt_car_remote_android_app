package com.unitbvcv.btcarremote

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.bluetooth.BluetoothDevice
import android.content.Intent
import java.lang.ref.WeakReference
import kotlin.experimental.or

class BluetoothViewModel : ViewModel() {
    var bluetoothService: WeakReference<BluetoothService?> = WeakReference(null)

    // TODO: create observers for the live data, either here or in another class

    val joystickObserver: Observer<Pair<Double, Double>> = Observer { joystickPair: Pair<Double, Double>? ->
        if (joystickPair != null) {
            val motorValues = toMotorValues(joystickPair)
            bluetoothService.get()?.sendJoystickData(motorValues)
        }
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


    private fun toMotorValues(joystickPair: Pair<Double, Double>): Pair<Byte, Byte> {

        val percentage = joystickPair.first
        val angle = joystickPair.second

        val alpha = 45.0
        val signFlag: Byte = 0b10000000.toByte()

        if (angle in 0.0..90.0) {
            // First quadrant
            return toMotorValuesFirstQuadrant(percentage, angle, alpha)
        }
        else if (angle in 90.0..180.0) {
            // Second quadrant
            val firstQValues = toMotorValuesFirstQuadrant(percentage, 180.0 - angle, alpha)
            return Pair(firstQValues.second, firstQValues.first)
        }
        else if (angle in 180.0..270.0) {
            // Third quadrant
            val firstQValues = toMotorValuesFirstQuadrant(percentage, angle - 180.0, alpha)
            return Pair(firstQValues.second.or(signFlag), firstQValues.first.or(signFlag))
        }
        else if (angle in 270.0..360.0) {
            // Fourth quadrant
            val firstQValues = toMotorValuesFirstQuadrant(percentage, 360.0 - angle, alpha)
            return Pair(firstQValues.first.or(signFlag), firstQValues.second.or(signFlag))
        }

        return Pair(0, 0)
    }


    private fun toMotorValuesFirstQuadrant(percentage: Double, angle: Double, alpha: Double): Pair<Byte, Byte> {

        val middleDelta = 2.0
        val signFlag: Byte = 0b10000000.toByte()

        if (angle >= 0) {
            if (angle <= 5.0) {
                // cazul 5
                return Pair(percentage.toByte(), percentage.toByte().or(signFlag))
            }
            else if (angle <= alpha - middleDelta) {
                // cazul 4
                val right = (angle - 5.0) / (alpha - middleDelta - 5.0) * percentage
                return Pair(percentage.toByte(), right.toByte().or(signFlag))
            }
            else if (angle <= alpha + middleDelta) {
                // cazul 3
                return Pair(percentage.toByte(), 0)
            }
            else if (angle <= 85.0) {
                // cazul 2
                val right = (angle - alpha - middleDelta) / (85.0 - alpha - middleDelta) * percentage
                return Pair(percentage.toByte(), right.toByte())
            }
            else if (angle <= 90) {
                // cazul 1
                return Pair(percentage.toByte(), percentage.toByte())
            }
        }

        return Pair(0, 0)
    }
}
