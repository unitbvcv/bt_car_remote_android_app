package com.unitbvcv.btcarremote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.Surface
import android.view.WindowManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLayout(getScreenOrientation())
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
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settingsItem -> {
                val intent = Intent(this, Settings::class.java).apply {
                    // putExtra(EXTRA_MESSAGE, message)
                }
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
