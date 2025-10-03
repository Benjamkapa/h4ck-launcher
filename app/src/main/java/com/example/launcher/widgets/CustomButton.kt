package com.example.launcher.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    init {
        // Customize button appearance here
        setTextColor(0xFF00FF00.toInt()) // Matrix green
        setBackgroundColor(0xFF000000.toInt()) // Black background
        isAllCaps = false
    }
}
