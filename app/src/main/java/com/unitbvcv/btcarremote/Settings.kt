package com.unitbvcv.btcarremote

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*


class Settings : AppCompatActivity() {

    private val menuList1: Array<String> = arrayOf("Connect to device")
    private val menuList2: Array<String> = arrayOf("Refresh", "Timeout")
    private val menuList3: Array<String> = arrayOf("About")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        populateListView1()
        populateListView2()
        populateListView3()

//        val a = findViewById<ListView>(R.id.bt_params_list_view)
//        val b = a.getChildAt(0)
//        val c = a.adapter.getView(0, null, a)
//
//        val s = c.findViewById<Spinner>(R.id.spinner)
//
//        val adapter = ArrayAdapter(this, R.layout.text_view, arrayListOf("ceva", "ceva2")).also {
//            it.setDropDownViewResource(R.layout.text_view)
//        }
//        s.adapter = adapter

    }

    private fun populateListView1() {
        val adapter1 = ArrayAdapter<String>(this,
            R.layout.text_view, menuList1)

        val listView = findViewById<ListView>(R.id.connect_device_list_view)
        listView.adapter = adapter1
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateListView2() {
        val adapter2 = ArrayAdapter<String>(this,
            R.layout.text_drop_down, R.id.setting_name_text_drop_down, menuList2)

        val listView = findViewById<ListView>(R.id.bt_params_list_view)
        listView.adapter = adapter2
    }

    private fun populateListView3() {
        val adapter3 = ArrayAdapter<String>(this,
            R.layout.text_view, menuList3)

        val listView = findViewById<ListView>(R.id.about_list_view)
        listView.adapter = adapter3
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, "test 3", Toast.LENGTH_SHORT).show()
        }
    }

}
