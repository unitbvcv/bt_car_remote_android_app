package com.unitbvcv.btcarremote

import android.graphics.Canvas
import android.graphics.Paint

class Circle(var centerX: Float, var centerY: Float, var radius: Float) {

    val painter: Paint = Paint()

    fun draw(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius, painter)
    }
}