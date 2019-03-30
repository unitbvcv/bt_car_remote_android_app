package com.unitbvcv.btcarremote

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel

    private val connectMenuValues: Array<String> = arrayOf("Connect to device")
    private val aboutMenuValues: Array<String> = arrayOf("About")

    private val refreshSpinnerValues: Array<String> = arrayOf("25", "50", "100", "500", "1000")
    private val timeoutSpinnerValues: Array<String> = arrayOf("1", "10", "100", "1000")


    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("refreshRate", settingsViewModel.refreshRate.toInt())
            putExtra("timeoutCount", settingsViewModel.timeoutCount.toInt())
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        processIntent()

        populateConnectListView()
        populateAboutListView()

        populateRefreshSpinner()
        populateTimeoutSpinner()
    }

    private fun processIntent() {
        settingsViewModel.refreshRate = intent.getStringExtra("refreshRate")
        settingsViewModel.timeoutCount = intent.getStringExtra("timeoutCount")
    }

    private fun populateConnectListView() {
        val adapter = ArrayAdapter<String>(this, R.layout.text_view, connectMenuValues)

        val connectListView = findViewById<ListView>(R.id.connect_device_list_view)
        connectListView.adapter = adapter

        connectListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
            // TODO: move to another activity with bluetooth device search
        }
    }

    private fun populateAboutListView() {
        val adapter = ArrayAdapter<String>(this, R.layout.text_view, aboutMenuValues)

        val aboutListView = findViewById<ListView>(R.id.about_list_view)
        aboutListView.adapter = adapter

        aboutListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            AboutDialog(this).show()
        }
    }

    private fun populateRefreshSpinner() {
        val refreshSpinner = findViewById<Spinner>(R.id.refresh_spinner)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, refreshSpinnerValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        refreshSpinner.adapter = adapter

        refreshSpinner.setSelection(refreshSpinnerValues.indexOf(settingsViewModel.refreshRate))

        refreshSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Empty
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (view != null && view is TextView) {
                    settingsViewModel.refreshRate = view.text.toString()
                }
            }
        }
    }

    private fun populateTimeoutSpinner() {
        val timeoutSpinner = findViewById<Spinner>(R.id.timeout_spinner)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeoutSpinnerValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeoutSpinner.adapter = adapter

        timeoutSpinner.setSelection(timeoutSpinnerValues.indexOf(settingsViewModel.timeoutCount))

        timeoutSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Empty
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (view != null && view is TextView) {
                    settingsViewModel.timeoutCount = view.text.toString()
                }
            }
        }
    }

}
