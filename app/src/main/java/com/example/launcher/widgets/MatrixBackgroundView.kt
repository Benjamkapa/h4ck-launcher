package com.example.launcher.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class MatrixBackgroundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val textSize = 20f

    private val paint = Paint().apply {
        color = Color.parseColor("#00FF00")
        textSize = this@MatrixBackgroundView.textSize
        typeface = android.graphics.Typeface.MONOSPACE
        isAntiAlias = true
    }

//    test

    private var numRows = 0
    private var numCols = 0
    private lateinit var matrixChars: Array<CharArray>
    private lateinit var xOffsets: FloatArray
    private lateinit var yPositions: FloatArray

    private val speed = 10f
    private var animating = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val charWidth = paint.measureText("0")
        val charHeight = paint.textSize

        numCols = (w / charWidth).toInt()
        numRows = (h / charHeight).toInt()

        matrixChars = Array(numRows) { CharArray(numCols) }

        // Fill random chars
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                matrixChars[row][col] = if (Random.nextBoolean()) '0' else '1'
            }
        }

        // lateral offsets
        xOffsets = FloatArray(numRows) { 0f }

        // y positions (start at top, row by row downward)
        yPositions = FloatArray(numRows) { row ->
            (row + 1) * charHeight
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val charWidth = paint.measureText("0")

        for (row in 0 until numRows) {
            val offset = xOffsets[row]

            for (col in 0 until numCols) {
                // pick a char dynamically, not static
                val char = if (Random.nextBoolean()) '0' else '1'

                var x = (col * charWidth - offset)
                if (x < -charWidth) {
                    x += numCols * charWidth
                }
                canvas.drawText(char.toString(), x, yPositions[row], paint)
            }

            // move this row independently
            xOffsets[row] += speed
            if (xOffsets[row] > charWidth * numCols) {
                xOffsets[row] = 0f
            }
        }

        if (animating) {
            postInvalidateOnAnimation()
        }
    }

    fun startAnimation() {
        animating = true
        invalidate()
    }

    fun stopAnimation() {
        animating = false
    }
}