package com.unitbvcv.btcarremote

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_connect.*

class ConnectActivity : AppCompatActivity() {

    private lateinit var connectViewModel: ConnectViewModel

    private val REQUEST_ENABLE_BT: Int = 4367
    private lateinit var bluetoothAdapter: BluetoothAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        setSupportActionBar(toolbar_connect)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        connectViewModel = ViewModelProviders.of(this).get(ConnectViewModel::class.java)

        processIntent()

        val btAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter != null) {
            bluetoothAdapter = btAdapter

            /*
             * observer pt paired View
             *
             * un observer pe lista care sa faca adapterul pt listview si apoi onItemSelected care sa highlightuiasca
             * si sa opreasca descoperirea si sa inchida conexiunea veche daca exista
             * daca se face pair atunci sa se faca refresh la paired devices - sparta functia de mai sus
             *
             * un broadcast receiver pe ACTION_FOUND care sa adauge in lista ce device am gasit
             *
             * un broadcast receiver pe DISCOREY_STARTED ca sa afiseze chestia rotativa si X-ul
             *
             * un broadcast receiver pe DISCOVERY_ENDED ca sa afiseze refreshul
             *
             * un menu.xml cu:
             * un buton de refresh
             * un buton de X
             * ceva rotativ - https://stackoverflow.com/questions/5442183/using-the-animated-circle-in-an-imageview-while-loading-stuff
             *
             */

            if (bluetoothAdapter.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
            else {
                populatePairedView()
                // populate discovered devices
            }
        }
    }

    private fun processIntent() {
        connectViewModel.refreshRate = intent.getStringExtra("refreshRate") ?: "50"
        connectViewModel.timeoutCount = intent.getStringExtra("timeoutCount") ?: "10"
    }

    override fun onBackPressed() {
        val intent = Intent(this, SettingsActivity::class.java).apply {
            putExtra("refreshRate", connectViewModel.refreshRate)
            putExtra("timeoutCount", connectViewModel.timeoutCount)
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
                        populatePairedView()
                        // populate discovered devices
                    }
                    Activity.RESULT_CANCELED -> {
                        onBackPressed()
                    }
                }
            }
        }
    }

    private fun populatePairedView() {
        // de spart in 2
        // observerul e de pus inainte de a verifica daca btAdapter e pornit
        connectViewModel.pairedDevicesList.observe(this, Observer { pairedDevicesList: List<String>? ->
            if (pairedDevicesList == null || pairedDevicesList.isEmpty()) {
                pairedTextView.visibility = View.GONE
                pairedListView.visibility = View.GONE
            } else {
                pairedTextView.visibility = View.VISIBLE
                pairedListView.visibility = View.VISIBLE

                // who is this???
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.text_view, pairedDevicesList)

                pairedListView.adapter = adapter

                pairedListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    // TODO: test connection maybe?
                    if (view is TextView) {
                        connectViewModel.deviceNameToConnect = view.text.toString()
                        Toast.makeText(this, connectViewModel.deviceNameToConnect, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        // doar astea 2 linii
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        connectViewModel.pairedDevicesList.value = pairedDevices?.map { it.name }
    }

    private fun populateDiscoveredDevices() {
        /*
         * de inchis conexiunea existenta
         * aici ar trebui sa se dea numai bluetoothAdapter.startDiscovery(), restul chestiilor sunt pregatiri inainte
         *
         */
    }
}
