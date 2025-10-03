package com.example.launcher.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.launcher.presentation.home.HomeActivity
import com.example.launcher.R

class SplashActivity : AppCompatActivity() {

    private val splashDisplayLength: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(mainLooper).postDelayed({
            val mainIntent = Intent(this@SplashActivity, HomeActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, splashDisplayLength)
    }
}
