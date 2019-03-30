package com.unitbvcv.btcarremote

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*

class ConnectActivity : AppCompatActivity() {

    private val REQUEST_ENABLE_BT: Int = 4367

    private var refreshRate: String = ""
    private var timeoutCount: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        processIntent()

        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
            else {
                // populate listviews
            }
        }
    }

    private fun processIntent() {
        refreshRate = intent.getStringExtra("refreshRate") ?: "50"
        timeoutCount = intent.getStringExtra("timeoutCount") ?: "10"
    }

    override fun onBackPressed() {
        val intent = Intent(this, SettingsActivity::class.java).apply {
            putExtra("refreshRate", refreshRate)
            putExtra("timeoutCount", timeoutCount)
        }
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        // populate list widgets
                    }
                    Activity.RESULT_CANCELED -> {
                        onBackPressed()
                    }
                }
            }
        }
    }
}
