package com.example.launcher.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.launcher.R

class FuturisticStatusBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val container: LinearLayout
    private val batteryIcon: ImageView
    private val chargingIcon: ImageView // Reference for the new icon

    // Receiver to listen for battery changes in real-time
    private val batteryStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateBatteryStatus(intent)
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_network_status_bar, this, true)
        container = findViewById(R.id.network_container)
        batteryIcon = findViewById(R.id.battery_icon)
        chargingIcon = findViewById(R.id.charging_icon) // Initialize the charging icon view

        setupNetworks()
        updateInitialBatteryState() // Get the initial state when the view is created
    }

    // This function checks the battery status and updates the UI
    private fun updateBatteryStatus(intent: Intent) {
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        if (isCharging) {
            // If charging, show the lightning icon and hide the battery level icon
            chargingIcon.visibility = View.VISIBLE
            batteryIcon.visibility = View.GONE
        } else {
            // If not charging, hide the lightning icon and show the battery level icon
            chargingIcon.visibility = View.GONE
            batteryIcon.visibility = View.VISIBLE

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            if (level != -1 && scale != -1) {
                val batteryPercent = (level * 100 / scale.toFloat()).toInt()
                batteryIcon.setImageResource(getBatteryDrawable(batteryPercent))
            }
        }
    }

    // Gets the initial state of the battery once
    private fun updateInitialBatteryState() {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, intentFilter)
        if (batteryStatus != null) {
            updateBatteryStatus(batteryStatus)
        }
    }

    // Register the receiver when the view is attached to a window (becomes visible)
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryStateReceiver, filter)
    }

    // Unregister the receiver when the view is detached to prevent memory leaks
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        context.unregisterReceiver(batteryStateReceiver)
    }

    private fun getBatteryDrawable(percent: Int): Int {
        return when {
            percent >= 90 -> R.drawable.battery_fill_100
            percent >= 70 -> R.drawable.battery_fill_75
            percent >= 40 -> R.drawable.battery_fill_50
            percent >= 20 -> R.drawable.battery_fill_25
            percent >= 10 -> R.drawable.battery_fill_10
            else -> R.drawable.ic_battery_outline
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun setupNetworks() {
        container.removeAllViews()

        val subMgr = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val teleMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val subscriptions = subMgr.activeSubscriptionInfoList ?: emptyList()

        if (subscriptions.isEmpty()) {
            addNetworkItem("No SIM", 0)
        } else {
            subscriptions.forEach { subInfo ->
                val signalStrength = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    teleMgr.createForSubscriptionId(subInfo.subscriptionId).signalStrength?.level ?: 0
                } else {
                    @Suppress("DEPRECATION")
                    teleMgr.signalStrength?.level ?: 0
                }
                addNetworkItem(subInfo.displayName.toString(), signalStrength)
            }
        }
    }

    private fun addNetworkItem(carrier: String, level: Int) {
        val item = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(8, 0, 8, 0)
        }

        val name = TextView(context).apply {
            text = carrier
            setTextColor(0xFF39FF14.toInt()) // Neon green
            textSize = 12f
        }

        val bars = ImageView(context).apply {
            setImageResource(getSignalDrawable(level))
            setPadding(6, 0, 0, 0)
        }

        item.addView(name)
        item.addView(bars)
        container.addView(item)
    }

    private fun getSignalDrawable(level: Int): Int {
        return when (level) {
            4 -> R.drawable.ic_signal_4
            3 -> R.drawable.ic_signal_3
            2 -> R.drawable.ic_signal_2
            1 -> R.drawable.ic_signal_1
            else -> R.drawable.ic_signal_0
        }
    }
}





















//package com.example.launcher.widgets
//
//import android.content.Context
//import android.os.Build
//import android.telephony.SubscriptionManager
//import android.telephony.TelephonyManager
//import android.util.AttributeSet
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.annotation.RequiresApi
//import com.example.launcher.R
//
//// --- FIX: RENAME THIS CLASS ---
//class FuturisticStatusBarView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null
//) : LinearLayout(context, attrs) {
//
//    private val container: LinearLayout
//
//    init {
//        LayoutInflater.from(context).inflate(R.layout.view_network_status_bar, this, true)
//        container = findViewById(R.id.network_container)
//        setupNetworks()
//
//        val batteryIcon = findViewById<ImageView>(R.id.battery_icon)
//        val bm = context.getSystemService(Context.BATTERY_SERVICE) as android.os.BatteryManager
//        val batteryPercent = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
//
//        batteryIcon.setImageResource(getBatteryDrawable(batteryPercent))
//    }
//
//    private fun getBatteryDrawable(percent: Int): Int {
//        // ... (rest of the function is correct)
//        return when {
//            percent >= 90 -> R.drawable.battery_fill_100
//            percent >= 70 -> R.drawable.battery_fill_75
//            percent >= 40 -> R.drawable.battery_fill_50
//            percent >= 20 -> R.drawable.battery_fill_25
//            percent >= 10 -> R.drawable.battery_fill_10
//            else -> R.drawable.ic_battery_outline
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
//    private fun setupNetworks() {
//        container.removeAllViews()
//
//        val subMgr = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
//        val teleMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//
//        val subscriptions = subMgr.activeSubscriptionInfoList ?: emptyList()
//
//        if (subscriptions.isEmpty()) {
//            addNetworkItem("No SIM", 0)
//        } else {
//            subscriptions.forEach { subInfo ->
//                val signalStrength = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//                    teleMgr.createForSubscriptionId(subInfo.subscriptionId).signalStrength?.level ?: 0
//                } else {
//                    teleMgr.signalStrength?.level ?: 0
//                }
//                addNetworkItem(subInfo.displayName.toString(), signalStrength)
//            }
//        }
//    }
//
//    private fun addNetworkItem(carrier: String, level: Int) {
//        val item = LinearLayout(context).apply {
//            orientation = HORIZONTAL
//            gravity = Gravity.CENTER_VERTICAL
////            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
//            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//            setPadding(8, 0, 8, 0)
//        }
//
//        val name = TextView(context).apply {
//            text = carrier
//            setTextColor(0xFF39FF14.toInt()) // Neon green
//            textSize = 12f
//        }
//
//        val bars = ImageView(context).apply {
//            setImageResource(getSignalDrawable(level))
//            setPadding(6, 0, 0, 0)
//        }
//
//        item.addView(name)
//        item.addView(bars)
//        container.addView(item)
//    }
//
//    private fun getSignalDrawable(level: Int): Int {
//        // ... (rest of the function is correct)
//        return when (level) {
//            4 -> R.drawable.ic_signal_4
//            3 -> R.drawable.ic_signal_3
//            2 -> R.drawable.ic_signal_2
//            1 -> R.drawable.ic_signal_1
//            else -> R.drawable.ic_signal_0
//        }
//    }
//}