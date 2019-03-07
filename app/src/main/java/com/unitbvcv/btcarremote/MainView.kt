package com.unitbvcv.btcarremote

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Debug
import android.util.AttributeSet
import android.util.Log
import android.view.View

class MainView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    /* Attribute */
//    private var _exampleString: String? = null

    /* Property */
//    var exampleString: String?
//        get() = _exampleString
//        set(value) {
//            _exampleString = value
//            invalidateTextPaintAndMeasurements()
//        }

    init {
        setWillNotDraw(false)
    }


    private val _painter: Paint = Paint().apply {
        color = Color.BLACK
        textSize = 100f
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawText("Test", 200f, 200f, _painter)
        canvas.drawCircle(0f, 0f, 100f, _painter)
    }

}
