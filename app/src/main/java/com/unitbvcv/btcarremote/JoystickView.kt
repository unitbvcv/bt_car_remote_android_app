package com.unitbvcv.btcarremote

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class JoystickView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val backCircle: Circle = Circle(0f, 0f, 200f).apply {
        painter.color = Color.GRAY
    }

    private val frontCircle: Circle = Circle(0f, 0f, 100f).apply {
        painter.color = Color.RED
    }

    private val maxRadius: Float = 450f

    private var circlesDistancePercentage: Double = 0.0
    private var slopeAngle: Double = 0.0

    val joystickData: MutableLiveData<Pair<Double, Double>> = MutableLiveData<Pair<Double, Double>>().also {
        it.value = Pair<Double, Double>(circlesDistancePercentage, slopeAngle)
    }

    init {
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        backCircle.centerX = w.toFloat() / 2
        backCircle.centerY = h.toFloat() / 2

        val minSide = min(w.toFloat(), h.toFloat())
        backCircle.radius = if (minSide / 2 < maxRadius) minSide / 2 else maxRadius

        frontCircle.centerX = backCircle.centerX
        frontCircle.centerY = backCircle.centerY
        frontCircle.radius = backCircle.radius / 2.5f

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        backCircle.draw(canvas)
        frontCircle.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val result = onTouchImpl(event)
        joystickData.value = Pair(circlesDistancePercentage, slopeAngle)
        return result
    }

    private fun onTouchImpl(event: MotionEvent?): Boolean {
        val maxPermittedDistance = backCircle.radius - frontCircle.radius
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x - backCircle.centerX
                val y = event.y - backCircle.centerY
                if (x*x + y*y <= maxPermittedDistance * maxPermittedDistance) {
                    frontCircle.centerX = event.x
                    frontCircle.centerY = event.y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x - backCircle.centerX
                val y = event.y - backCircle.centerY
                if (x*x + y*y <= maxPermittedDistance * maxPermittedDistance) {
                    frontCircle.centerX = event.x
                    frontCircle.centerY = event.y
                }
                else if (!(frontCircle.centerX == backCircle.centerX && frontCircle.centerY == backCircle.centerY &&
                            x*x + y*y > backCircle.radius * backCircle.radius)) {
                    val slopeAngle = (Math.atan2(y.toDouble(), x.toDouble()) + 2 * Math.PI)
                    frontCircle.centerX = maxPermittedDistance * Math.cos(slopeAngle).toFloat() + backCircle.centerX
                    frontCircle.centerY = maxPermittedDistance * Math.sin(slopeAngle).toFloat() + backCircle.centerY
                }
            }
            MotionEvent.ACTION_UP -> {
                frontCircle.centerX = backCircle.centerX
                frontCircle.centerY = backCircle.centerY
            }
            else -> return false // does it matter?
        }

        updateCircleCentersRelativePolarCoordinates()
        invalidate()
        return true
    }

    private fun updateCircleCentersRelativePolarCoordinates() {
        val maxPermittedDistance = backCircle.radius - frontCircle.radius

        circlesDistancePercentage = Math.hypot(
            (frontCircle.centerX - backCircle.centerX).toDouble(),
            (frontCircle.centerY - backCircle.centerY).toDouble()
        ) / maxPermittedDistance * 100

        slopeAngle = Math.atan2(
            -(frontCircle.centerY - backCircle.centerY).toDouble(),
            (frontCircle.centerX - backCircle.centerX).toDouble()
        )
        if (slopeAngle < 0)
            slopeAngle += 2 * Math.PI
        slopeAngle = Math.toDegrees(slopeAngle)
    }

}
