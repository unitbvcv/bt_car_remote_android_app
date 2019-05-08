package com.unitbvcv.btcarremote

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
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

    private val REQUEST_BT_SETTINGS = 4311

    private var isActionFoundReceiverRegistered = false
    private lateinit var actionFoundReceiver: ActionFoundReceiver

    private var isDiscoveryStartedReceiverRegistered = false
    private lateinit var actionDiscoveryStartedReceiver: ActionDiscoveryStartedReceiver

    private var isDiscoveryFinishedReceiverRegistered = false
    private lateinit var actionDiscoveryFinishedReceiver: ActionDiscoveryFinishedReceiver

    private var bluetoothService: BluetoothService? = null
    private var isServiceBound = false
    private var serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is BluetoothService.BluetoothServiceBinder) {
                bluetoothService = service.getService()
                isServiceBound = true
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bluetoothService = null
            isServiceBound = false
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
            if (!extraCheckPermissions()) {
                return
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initializeBluetooth()
        doBindService()
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
                        startDiscovery()
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

            var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            actionFoundReceiver = ActionFoundReceiver(connectViewModel)
            registerReceiver(actionFoundReceiver, filter)
            isActionFoundReceiverRegistered = true

            filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            actionDiscoveryStartedReceiver = ActionDiscoveryStartedReceiver()
            registerReceiver(actionDiscoveryStartedReceiver, filter)
            isDiscoveryStartedReceiverRegistered = true

            filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            actionDiscoveryFinishedReceiver = ActionDiscoveryFinishedReceiver()
            registerReceiver(actionDiscoveryFinishedReceiver, filter)
            isDiscoveryFinishedReceiverRegistered = true

            if (bluetoothAdapter.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                populatePairedView()
                startDiscovery()
            }
        }
    }

    private fun doBindService() {
        val intent = Intent(this, BluetoothService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun doUnbindService() {
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
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

                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.text_view, pairedDevicesList)

                pairedListView.adapter = adapter

                pairedListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    if (view is TextView) {
                        val deviceToConnectTo = bluetoothAdapter.bondedDevices.find {
                            "${it.name} ${it.address}" == view.text.toString()
                        }

                        stopDiscovery()

                        if (deviceToConnectTo != null) {
                            bluetoothService?.connectToBluetoothDevice(deviceToConnectTo)
                        }
                    }
                }
            }
        })
    }

    private fun populatePairedView() {
        connectViewModel.pairedDevicesList.value = listOf()
        pairedListView.adapter = ArrayAdapter<String>(this, R.layout.text_view, emptyArray())

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
                        // TODO: pairing dialog (below it shows but it doesn't add the device to the paired list)
//                        val pairingIntent = Intent(BluetoothDevice.ACTION_PAIRING_REQUEST)
//                        pairingIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, deviceToConnectTo)
//                        pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.PAIRING_VARIANT_PIN)
//                        pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_KEY, 1234)
//                        startActivityForResult(pairingIntent, REQUEST_BT_SETTINGS)
                        Toast.makeText(this, "Please pair with this device in your phone's Bluetooth menu before connecting", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }

    private fun startDiscovery() {
        // TODO: de oprit vreo conexiune existenta

        connectViewModel.discoveredDevicesList.value = listOf()
        connectViewModel.bluetoothDevicesMap.clear()
        nearbyListView.adapter = ArrayAdapter<String>(this, R.layout.text_view, emptyArray())

        bluetoothAdapter.startDiscovery()
    }

    private fun stopDiscovery() {
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
    }

    private fun unregisterReceivers() {
        if (isActionFoundReceiverRegistered) {
            unregisterReceiver(actionFoundReceiver)
            isActionFoundReceiverRegistered = false
        }
        if (isDiscoveryStartedReceiverRegistered) {
            unregisterReceiver(actionDiscoveryStartedReceiver)
            isDiscoveryStartedReceiverRegistered = false
        }
        if (isDiscoveryFinishedReceiverRegistered) {
            unregisterReceiver(actionDiscoveryFinishedReceiver)
            isDiscoveryFinishedReceiverRegistered = false
        }
    }

    override fun onBackPressed() {
        stopDiscovery()
        unregisterReceivers()

        val intent = Intent(this, SettingsActivity::class.java).apply {
            putExtra("refreshRate", connectViewModel.refreshRate)
            putExtra("timeoutCount", connectViewModel.timeoutCount)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.connect_activity_menu, menu)

        menu?.findItem(R.id.refreshDiscoveryItem)?.isVisible = false

        val view = menu?.findItem(R.id.loadingDiscoveryItem)?.actionView
        if (view != null && view is ProgressBar) {
            view.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        }

        actionDiscoveryStartedReceiver.menu = menu
        actionDiscoveryFinishedReceiver.menu = menu

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.refreshDiscoveryItem -> {
                populatePairedView()
                startDiscovery()
                true
            }
            R.id.stopDiscoveryItem -> {
                stopDiscovery()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        stopDiscovery()
        unregisterReceivers()
        doUnbindService()

        super.onDestroy()
    }


}
