package com.example.launcher.presentation.home

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.launcher.R
import com.example.launcher.widgets.MatrixBackgroundView
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.provider.Settings
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.launcher.AppDrawerAdapter
import com.example.launcher.AppInfo

class HomeActivity : AppCompatActivity() {

    private lateinit var matrixBackgroundView: MatrixBackgroundView
    // Add the declaration for your RecyclerView here
    private lateinit var appListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This allows your content to be drawn behind the system bars.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set system bars to be transparent.
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_home)
        hideSystemBars()

        checkAndRequestNotificationPermission()

        matrixBackgroundView = findViewById(R.id.matrix_background_view)
        matrixBackgroundView.alpha = 0.3f // Set Opacity to 30%
        matrixBackgroundView.startAnimation()

        // --- NEW CODE to set up app list ---
        appListRecyclerView = findViewById(R.id.app_list_rv)
        appListRecyclerView.layoutManager = GridLayoutManager(this, 5) // 4 columns

        val apps = loadApps()
        appListRecyclerView.adapter = AppDrawerAdapter(apps)
    }

    private fun checkAndRequestNotificationPermission() {
        val enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val isEnabled = enabledListeners?.contains(packageName) == true

        if (!isEnabled) {
            // Permission is not granted, guide user to settings
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
    }

//    // --- NEW FUNCTION to load apps ---
//    private fun loadApps(): List<AppInfo> {
//        val pm = packageManager
//        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
//            addCategory(Intent.CATEGORY_LAUNCHER)
//        }
//        val resolveInfos: List<ResolveInfo> = pm.queryIntentActivities(mainIntent, 0)
//
//        val appsList = ArrayList<AppInfo>()
//        for (info in resolveInfos) {
//            appsList.add(
//                AppInfo(
//                    label = info.loadLabel(pm),
//                    packageName = info.activityInfo.packageName,
//                    icon = info.loadIcon(pm)
//                )
//            )
//        }
//        // Sort the list alphabetically
//        appsList.sortBy { it.label.toString().lowercase() }
//        return appsList
//    }



    private fun loadApps(): List<AppInfo> {
        val pm = packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        // Use the new ResolveInfoFlags API (requires API 33+)
        val resolveInfos: List<ResolveInfo> = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(mainIntent, 0)
        }

        val appsList = ArrayList<AppInfo>()
        for (info in resolveInfos) {
            appsList.add(
                AppInfo(
                    label = info.loadLabel(pm),
                    packageName = info.activityInfo.packageName,
                    icon = info.loadIcon(pm)
                )
            )
        }

        appsList.sortBy { it.label.toString().lowercase() }
        return appsList
    }




    // Add this private helper function
    private fun hideSystemBars() {
        val windowInsetsController =
            WindowInsetsControllerCompat(window, window.decorView)
        // Hide the system bars (status bar and navigation bar).
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        // Configure the behavior for when the user swipes to reveal the system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onDestroy() {
        super.onDestroy()
        matrixBackgroundView.stopAnimation()
    }
}
