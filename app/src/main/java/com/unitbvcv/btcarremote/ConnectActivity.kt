package com.unitbvcv.btcarremote

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
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

    private lateinit var bluetoothAdapter: BluetoothAdapter

    private val REQUEST_ENABLE_BT: Int = 4367

    private val REQUEST_COARSE_LOCATION = 4299
    private var isCoarseLocationPermitted = false

    private val REQUEST_BLUETOOTH = 4304
    private var isBluetoothPermitted = false

    private val REQUEST_BLUETOOTH_ADMIN = 4306
    private var isBluetoothAdminPermitted = false


    private var isActionFoundReceiverRegistered = false
    // de scos
    private val actionFoundReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val list = connectViewModel.discoveredDevicesList.value?.toMutableList() ?: mutableListOf<String>()
                    list += "${device.name} ${device.address}"
                    connectViewModel.discoveredDevicesList.value = list
                }
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        setSupportActionBar(toolbar_connect)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        connectViewModel = ViewModelProviders.of(this).get(ConnectViewModel::class.java)

        processIntent()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (extraCheckPermissions()) {
                initializeBluetooth()
            }
        } else {
            initializeBluetooth()
        }
    }

    private fun processIntent() {
        connectViewModel.refreshRate = intent.getStringExtra("refreshRate") ?: "50"
        connectViewModel.timeoutCount = intent.getStringExtra("timeoutCount") ?: "10"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun extraCheckPermissions() : Boolean {
        isCoarseLocationPermitted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!isCoarseLocationPermitted) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_COARSE_LOCATION)
        }

        isBluetoothPermitted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        if (!isBluetoothPermitted) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH), REQUEST_BLUETOOTH)
        }

        isBluetoothAdminPermitted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
        if (!isBluetoothPermitted) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_ADMIN), REQUEST_BLUETOOTH_ADMIN)
        }

        return isCoarseLocationPermitted && isBluetoothPermitted && isBluetoothAdminPermitted
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_COARSE_LOCATION -> {
                isCoarseLocationPermitted =
                    !grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
            REQUEST_BLUETOOTH -> {
                isBluetoothPermitted =
                    !grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
            REQUEST_BLUETOOTH_ADMIN -> {
                isBluetoothAdminPermitted =
                    !grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }

        if (isCoarseLocationPermitted && isBluetoothPermitted && isBluetoothAdminPermitted) {
            initializeBluetooth()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        populatePairedView()
                        populateDiscoveredDevices()
                    }
                    Activity.RESULT_CANCELED -> {
                        onBackPressed()
                    }
                }
            }
        }
    }

    private fun initializeBluetooth() {
        val btAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter != null) {
            bluetoothAdapter = btAdapter

            createPairedViewObserver()
            createNearbyViewObserver()

            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            //            actionFoundReceiver = ActionFoundReceiver().apply {
            //                viewModel = connectViewModel
            //            }
            registerReceiver(actionFoundReceiver, filter)
            isActionFoundReceiverRegistered = true


            /*
             *
             * un observer pe lista care sa faca adapterul pt listview si apoi onItemSelected care sa highlightuiasca
             * si sa opreasca descoperirea si sa inchida conexiunea veche daca exista
             * daca se face pair atunci sa se faca refresh la paired devices - sparta functia de mai sus
             *
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
            } else {
                populatePairedView()
                populateDiscoveredDevices()
            }
        }
    }

    private fun createPairedViewObserver() {
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

                        bluetoothAdapter.cancelDiscovery()

                        Toast.makeText(this, connectViewModel.deviceNameToConnect, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun populatePairedView() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        connectViewModel.pairedDevicesList.value = pairedDevices?.map { it.name + " " + it.address }
    }

    private fun createNearbyViewObserver() {
        connectViewModel.discoveredDevicesList.observe(this, Observer { discoveredDevicesList: List<String>? ->
            if (discoveredDevicesList != null && discoveredDevicesList.isEmpty() == false) {
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.text_view, discoveredDevicesList)

                nearbyListView.adapter = adapter

                nearbyListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    if (view is TextView) {
                        // TODO: pair and test connection maybe
                        connectViewModel.deviceNameToConnect = view.text.toString()

                        bluetoothAdapter.cancelDiscovery()

                        Toast.makeText(this, connectViewModel.deviceNameToConnect, Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }

    private fun populateDiscoveredDevices() {
        bluetoothAdapter.startDiscovery()
    }

    private fun unregisterReceivers() {
        if (isActionFoundReceiverRegistered) {
            unregisterReceiver(actionFoundReceiver)
            isActionFoundReceiverRegistered = false
        }
    }

    override fun onBackPressed() {
        unregisterReceivers()

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

    override fun onDestroy() {
        unregisterReceivers()
        super.onDestroy()
    }

}
