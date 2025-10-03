package com.example.launcher.routes

import android.content.Context
import android.content.Intent
import com.example.launcher.presentation.home.HomeActivity
import com.example.launcher.presentation.splash.SplashActivity

object AppRoutes {

    fun navigateToSplash(context: Context) {
        val intent = Intent(context, SplashActivity::class.java)
        context.startActivity(intent)
    }

    fun navigateToHome(context: Context) {
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent)
    }
}
