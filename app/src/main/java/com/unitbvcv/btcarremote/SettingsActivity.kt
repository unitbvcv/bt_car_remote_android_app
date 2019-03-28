package com.unitbvcv.btcarremote

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    private val connectMenuValues: Array<String> = arrayOf("Connect to device")
    private val aboutMenuValues: Array<String> = arrayOf("About")

    private val refreshSpinnerValues: Array<String> = arrayOf("25", "50", "100", "500", "1000")
    private val timeoutSpinnerValues: Array<String> = arrayOf("1", "10", "100", "1000")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        populateConnectListView()
        populateAboutListView()

        populateRefreshSpinner()
        populateTimeoutSpinner()
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

        refreshSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Empty
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null && view != null && view is TextView)
                {
                    Toast.makeText(parent.context, view.text, Toast.LENGTH_SHORT).show()
                }
                // TODO: set the refresh rate
                // either here, or extract this listener to another class
            }

        }
    }

    private fun populateTimeoutSpinner() {
        val timeoutSpinner = findViewById<Spinner>(R.id.timeout_spinner)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeoutSpinnerValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeoutSpinner.adapter = adapter

        timeoutSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Empty
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null && view != null && view is TextView)
                {
                    Toast.makeText(parent.context, view.text, Toast.LENGTH_SHORT).show()
                }
                // TODO: set the timeout
                // either here, or extract this listener to another class
            }

        }
    }

}
