package com.example.launcher.presentation.home

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.launcher.R
import com.example.launcher.widgets.MatrixBackgroundView

class HomeActivity : AppCompatActivity() {

    private lateinit var matrixBackgroundView: MatrixBackgroundView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This allows your content to be drawn behind the system bars.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set system bars to be transparent.
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_home)

        // This is the replacement for hideSystemBars()
        hideSystemBars()

        matrixBackgroundView = findViewById(R.id.matrix_background_view)
        matrixBackgroundView.startAnimation()
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












//package com.example.launcher.presentation.home
//
//import android.graphics.Color
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.WindowCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat
//import com.example.launcher.R
//import com.example.launcher.widgets.MatrixBackgroundView
//
//class HomeActivity : AppCompatActivity() {
//
//    private lateinit var matrixBackgroundView: MatrixBackgroundView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        hideSystemBars()
//
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        window.statusBarColor = Color.TRANSPARENT
//        window.navigationBarColor = Color.TRANSPARENT
//
//        setContentView(R.layout.activity_home)
//
//        matrixBackgroundView = findViewById(R.id.matrix_background_view)
//        matrixBackgroundView.startAnimation()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        matrixBackgroundView.stopAnimation()
//    }
//}
