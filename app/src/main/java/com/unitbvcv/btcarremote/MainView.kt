package com.unitbvcv.btcarremote

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.min

class MainView(context: Context, attrs: AttributeSet) : View(context, attrs) {

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

    private val _painter: Paint = Paint()

    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private val maxRadius: Float = 400f
    private var backCircleRadius: Float = 200f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = w.toFloat() / 2
        centerY = h.toFloat() / 2

        val minSide = min(w.toFloat(), h.toFloat())
        backCircleRadius = if (minSide / 2 < maxRadius) minSide / 2 else maxRadius

        for (i in 1..100){
            Log.i("Test: minSide = ", minSide.toString())
            Log.i("Test: backCircleRad = ", backCircleRadius.toString())
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawCircle(centerX, centerY, backCircleRadius, _painter.apply {
            color = Color.GRAY
        })
    }

}
