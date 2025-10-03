package com.example.launcher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.launcher.presentation.splash.SplashActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Launch SplashActivity as the entry point
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }
}
