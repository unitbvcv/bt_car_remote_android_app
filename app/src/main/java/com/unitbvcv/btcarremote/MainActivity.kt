package com.unitbvcv.btcarremote

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.Surface
import android.view.WindowManager
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothViewModel: BluetoothViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLayout(getScreenOrientation())

        bluetoothViewModel = ViewModelProviders.of(this).get(BluetoothViewModel::class.java)

        val joystickView = findViewById<JoystickView>(R.id.joystickView)

        joystickView.joystickData.observe(this, bluetoothViewModel.joystickObserver)

        // this updates the text view showing the joystick coordinates
        joystickView.joystickData.observe(this, Observer { joystickPair: Pair<Double, Double>? ->
            if (joystickPair != null) {
                findViewById<TextView>(R.id.textViewTouchCoord)?.text =
                    "(${joystickPair.first.toInt()}, ${joystickPair.second.toInt()})"
            }
        })

        processIntent()
    }

    private fun processIntent() {
        bluetoothViewModel.refreshRate.value = intent.getIntExtra("refreshRate", 50)
        bluetoothViewModel.timeoutCount.value = intent.getIntExtra("timeoutCount", 10)
    }

    private fun getScreenOrientation() : Int {
        val windowService = baseContext.getSystemService(Context.WINDOW_SERVICE)
        if (windowService is WindowManager) {
            return windowService.defaultDisplay.rotation
        }
        // else it is null or some other class for some reason
        return Surface.ROTATION_0
    }

    private fun loadLayout(screenRotation: Int) {
        when (screenRotation) { // 180 doesn't do anything on LG G4 :\
            Surface.ROTATION_0, Surface.ROTATION_180   -> { setContentView(R.layout.activity_main_portrait)  }
            Surface.ROTATION_90, Surface.ROTATION_270  -> { setContentView(R.layout.activity_main_landscape) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settingsItem -> {
                val intent = Intent(this, SettingsActivity::class.java).apply {
                    putExtra("refreshRate", bluetoothViewModel.refreshRate.value.toString())
                    putExtra("timeoutCount", bluetoothViewModel.timeoutCount.value.toString())
                }
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
