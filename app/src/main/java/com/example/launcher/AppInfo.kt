package com.example.launcher

// In app/src/main/java/com/example/launcher/AppInfo.kt

import android.graphics.drawable.Drawable

data class AppInfo(
    val label: CharSequence,
    val packageName: CharSequence,
    val icon: Drawable
)