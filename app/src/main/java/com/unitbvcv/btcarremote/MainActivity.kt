package com.unitbvcv.btcarremote

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

    override fun onStart() {
        super.onStart()

    }
}
