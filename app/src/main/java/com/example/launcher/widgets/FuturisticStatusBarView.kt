package com.example.launcher.widgets

import android.content.*
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import com.example.launcher.R
import java.util.*

@RequiresApi(Build.VERSION_CODES.M)
class FuturisticStatusBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var batteryLevel = 0
    private var currentTime = ""
    private var carriers: List<String> = listOf("No SIM")

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#00FF00") // neon green
        textSize = 28f
        typeface = Typeface.MONOSPACE
    }

    private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.TRANSPARENT
        style = Paint.Style.STROKE
        strokeWidth = 3f
        this.color = Color.parseColor("#00FF00")
    }

    private val handler = Handler(Looper.getMainLooper())

    private val systemReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_CHANGED -> updateBattery(intent)
            }
            invalidate()
        }
    }

    private fun updateBattery(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level != -1 && scale != -1) {
            batteryLevel = (level * 100 / scale.toFloat()).toInt()
        }
    }

    private fun updateCarriers() {
        val subs = SubscriptionManager.from(context).activeSubscriptionInfoList
        carriers = subs?.map { it.carrierName.toString() } ?: listOf("No SIM")
    }

    private val timeUpdater = object : Runnable {
        override fun run() {
            currentTime = DateFormat.format("HH:mm", Date()).toString()
            updateCarriers()
            invalidate()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(systemReceiver, filter)
        handler.post(timeUpdater)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        context.unregisterReceiver(systemReceiver)
        handler.removeCallbacks(timeUpdater)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = 20f
        val textY = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)

        // 1. Time (LEFT)
        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(currentTime, padding, textY, textPaint)

        // 2. Carrier + Battery (RIGHT)
        textPaint.textAlign = Paint.Align.RIGHT
        var rightX = width - padding

        carriers.forEachIndexed { index, carrier ->
            val boxWidth = 200f
            val boxHeight = 70f
            val left = rightX - boxWidth
            val top = (height / 2f) - (boxHeight / 2f)
            val rect = RectF(left, top, rightX, top + boxHeight)

            // Draw rounded box
            canvas.drawRoundRect(rect, 15f, 15f, boxPaint)

            // Carrier name
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                carrier,
                rect.centerX(),
                rect.centerY() - 10,
                textPaint
            )

            // Battery simple bar under name
            val batteryText = "$batteryLevel%"
            canvas.drawText(
                batteryText,
                rect.centerX(),
                rect.centerY() + 20,
                textPaint
            )

            // Shift left if multiple SIMs
            rightX -= (boxWidth + 20f)
        }
    }
}
















//package com.example.launcher.widgets
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.drawable.Icon
//import android.net.ConnectivityManager
//import android.net.NetworkCapabilities
//import android.os.BatteryManager
//import android.os.Build
//import android.os.Handler
//import android.os.Looper
//import android.telephony.TelephonyManager
//import android.text.format.DateFormat
//import android.util.AttributeSet
//import android.view.View
//import androidx.annotation.RequiresApi
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.example.launcher.services.ArcNotificationListener
//import java.util.*
//
//@RequiresApi(Build.VERSION_CODES.M)
//class FuturisticStatusBarView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//
//    private var batteryLevel = 0
//    private var currentTime = ""
//    private var networkType = "---"
//    private var carrierName = "No Service"
//    private var notificationIcons = listOf<Icon>()
//
//    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = Color.parseColor("#00FF00") // Matrix Green
//        textSize = 45f
//        typeface = android.graphics.Typeface.MONOSPACE
//    }
//
//    private val handler = Handler(Looper.getMainLooper())
//
//    // --- RECEIVER FOR NOTIFICATIONS, BATTERY, AND NETWORK ---
//    private val systemInfoReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            when (intent?.action) {
//                Intent.ACTION_BATTERY_CHANGED -> updateBattery(intent)
//                ArcNotificationListener.ACTION_SEND_NOTIFICATIONS -> updateNotifications(intent)
//                ConnectivityManager.CONNECTIVITY_ACTION -> updateNetworkInfo()
//            }
//            invalidate() // Redraw the view on any update
//        }
//    }
//
//    private fun updateBattery(intent: Intent) {
//        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
//        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
//        if (level != -1 && scale != -1) {
//            batteryLevel = (level * 100 / scale.toFloat()).toInt()
//        }
//    }
//
//    private fun updateNotifications(intent: Intent) {
//        val icons = intent.getParcelableArrayListExtra<Icon>(ArcNotificationListener.EXTRA_NOTIFICATIONS)
//        notificationIcons = icons ?: emptyList()
//    }
//
//    private fun updateNetworkInfo() {
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
//        networkType = when {
//            capabilities == null -> "---"
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "LTE"
//            else -> "NET"
//        }
//
//        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        carrierName = tm.networkOperatorName.take(8) // Limit carrier name length
//    }
//
//    private val timeUpdater = object : Runnable {
//        override fun run() {
//            currentTime = DateFormat.format("HH:mm", Date()).toString() // Changed to HH:mm
//            updateNetworkInfo() // Periodically check network status
//            invalidate()
//            handler.postDelayed(this, 1000)
//        }
//    }
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        val filter = IntentFilter().apply {
//            addAction(Intent.ACTION_BATTERY_CHANGED)
//            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
//        }
//        context.registerReceiver(systemInfoReceiver, filter)
//        // Register for local broadcasts from our notification service
//        LocalBroadcastManager.getInstance(context).registerReceiver(systemInfoReceiver, IntentFilter(
//            ArcNotificationListener.ACTION_SEND_NOTIFICATIONS))
//
//        handler.post(timeUpdater)
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        context.unregisterReceiver(systemInfoReceiver)
//        LocalBroadcastManager.getInstance(context).unregisterReceiver(systemInfoReceiver)
//        handler.removeCallbacks(timeUpdater)
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        canvas.drawColor(Color.argb(100, 0, 15, 0)) // Semi-transparent green background
//
//        val padding = 20f
//        val iconSize = (height * 0.6f).toInt()
//        val textY = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
//
//        // 1. Draw Notification Icons on the left
//        var currentX = padding
//        notificationIcons.take(5).forEach { icon -> // Show a max of 5 icons
//            val drawable = icon.loadDrawable(context)
//            drawable?.let {
//                it.setBounds(currentX.toInt(), (height - iconSize) / 2, currentX.toInt() + iconSize, (height + iconSize) / 2)
//                it.setTint(textPaint.color) // Tint the notification icon green
//                it.draw(canvas)
//                currentX += iconSize + 10 // Add padding between icons
//            }
//        }
//
//        // 2. Draw Time in the center
//        textPaint.textAlign = Paint.Align.CENTER
//        canvas.drawText(currentTime, width / 2f, textY, textPaint)
//
//        // 3. Draw Network, Carrier, and Battery on the right
//        textPaint.textAlign = Paint.Align.RIGHT
//        val rightText = "$carrierName | $networkType | BAT: $batteryLevel%"
//        canvas.drawText(rightText, width - padding, textY, textPaint)
//    }
//}
