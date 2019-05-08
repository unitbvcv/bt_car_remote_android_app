package com.unitbvcv.btcarremote

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import java.io.IOException
import java.util.*

class BluetoothService : Service() {
    private val binder = BluetoothServiceBinder()
    private val BLUETOOTH_SERIAL_BOARD_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val TRA_END = 3
    private val TRA_START = 2
    private val TRA_TYPE_LCD = 24
    private val TRA_TYPE_LED = 21
    private val TRA_TYPE_JOYSTICK = 23
    private val TRA_TYPE_SPEAKER = 22
    private var deviceToConnectTo: BluetoothDevice? = null
    private var bluetoothSocket: BluetoothSocket? = null

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothSocket?.close()
    }

    fun connectToBluetoothDevice(bluetoothDevice: BluetoothDevice): Boolean {
        if (deviceToConnectTo != bluetoothDevice) {
            deviceToConnectTo = bluetoothDevice

            try {
                bluetoothSocket = deviceToConnectTo?.createRfcommSocketToServiceRecord(BLUETOOTH_SERIAL_BOARD_UUID)
            } catch (exception: IOException) {
                Toast.makeText(this, "Couldn't retrieve a Bluetooth Socket for the given device", Toast.LENGTH_SHORT)
                    .show()
                return false
            }

            try {
                bluetoothSocket?.connect()
            } catch (exception: IOException) {
                Toast.makeText(
                    this,
                    "Couldn't connect to the Bluetooth Socket for the given device",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }

        return true
    }

    fun sendJoystickData(data: Pair<Byte, Byte>) {
        bluetoothSocket?.outputStream?.write(TRA_START)
        bluetoothSocket?.outputStream?.write(TRA_TYPE_JOYSTICK)
        bluetoothSocket?.outputStream?.write(data.first.toInt())
        bluetoothSocket?.outputStream?.write(data.second.toInt())
        bluetoothSocket?.outputStream?.write(TRA_END)
    }

    inner class BluetoothServiceBinder: Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }
}
