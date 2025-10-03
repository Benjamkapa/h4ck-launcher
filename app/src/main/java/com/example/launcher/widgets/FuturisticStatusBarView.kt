package com.example.launcher.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import java.util.*

class FuturisticStatusBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var batteryLevel = 0
    private var currentTime = ""

    // --- Paint objects for drawing ---
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#00FF00") // Matrix Green
        textSize = 45f // Larger text for the bar
        typeface = android.graphics.Typeface.MONOSPACE
    }

    private val handler = Handler(Looper.getMainLooper())

    // --- BroadcastReceiver to get battery updates ---
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level != -1 && scale != -1) {
                batteryLevel = (level * 100 / scale.toFloat()).toInt()
                invalidate() // Redraw the view
            }
        }
    }

    // --- Runnable to update the time every second ---
    private val timeUpdater = object : Runnable {
        override fun run() {
            currentTime = DateFormat.format("HH:mm:ss", Date()).toString()
            invalidate()
            handler.postDelayed(this, 1000) // Schedule again in 1 second
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Register the receiver to listen for battery changes
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        // Start updating the time
        handler.post(timeUpdater)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Unregister to prevent memory leaks
        context.unregisterReceiver(batteryReceiver)
        handler.removeCallbacks(timeUpdater)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Set a semi-transparent background for the bar itself
        canvas.drawColor(Color.argb(100, 0, 15, 0))

        val padding = 20f
        val textHeight = textPaint.descent() - textPaint.ascent()
        val textY = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)

        // Draw Time on the left
        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(currentTime, padding, textY, textPaint)

        // Draw Battery on the right
        textPaint.textAlign = Paint.Align.RIGHT
        val batteryText = "BAT: $batteryLevel%"
        canvas.drawText(batteryText, width - padding, textY, textPaint)
    }
}
