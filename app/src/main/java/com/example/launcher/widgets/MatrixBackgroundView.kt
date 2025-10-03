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
        color = Color.GREEN
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

    private val speed = 0.2f
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












//package com.example.launcher.widgets
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.util.AttributeSet
//import android.view.View
//import kotlin.random.Random
//
//class MatrixBackgroundView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//
//    private val textSize = 20f
//
//    private val paint = Paint().apply {
//        color = Color.GREEN
//        textSize = this@MatrixBackgroundView.textSize
//        typeface = android.graphics.Typeface.MONOSPACE
//        isAntiAlias = true
//    }
//
//    private var numRows = 0
//    private var numCols = 0
//    private lateinit var matrixChars: Array<CharArray>
//    private lateinit var xPositions: FloatArray
//    private lateinit var yPositions: FloatArray
//
//    // 👇 each row scrolls independently
//    private lateinit var offsetX: FloatArray
//    private val speed = 5f
//    private var animating = false
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        val charWidth = paint.measureText("0")
//        val charHeight = paint.textSize
//
//        numCols = (w / charWidth).toInt()
//        numRows = (h / charHeight).toInt()
//
//        matrixChars = Array(numRows) { CharArray(numCols) }
//        xPositions = FloatArray(numCols)
//        yPositions = FloatArray(numRows)
//        offsetX = FloatArray(numRows) { Random.nextFloat() * w } // random start offset for each row
//
//        // fill with random chars
//        for (row in 0 until numRows) {
//            for (col in 0 until numCols) {
//                matrixChars[row][col] = if (Random.nextBoolean()) '0' else '1'
//            }
//        }
//
//        // column x positions
//        for (col in 0 until numCols) {
//            xPositions[col] = col * charWidth
//        }
//        // row y positions
//        for (row in 0 until numRows) {
//            yPositions[row] = (row + 1) * charHeight
//        }
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        val charWidth = paint.measureText("0")
//        val viewWidth = width
//
//        // draw row by row
//        for (row in 0 until numRows) {
//            for (col in 0 until numCols) {
//                var x = xPositions[col] - offsetX[row]  // 👈 use per-row offset
//                if (x < -charWidth) {
//                    x += numCols * charWidth
//                    matrixChars[row][col] = if (Random.nextBoolean()) '0' else '1'
//                }
//                canvas.drawText(matrixChars[row][col].toString(), x, yPositions[row], paint)
//            }
//        }
//
//        if (animating) {
//            // move each row independently
//            for (row in 0 until numRows) {
//                offsetX[row] += speed
//                if (offsetX[row] > viewWidth) {
//                    offsetX[row] = 0f
//                }
//            }
//            postInvalidateOnAnimation()
//        }
//    }
//
//    fun startAnimation() {
//        animating = true
//        invalidate()
//    }
//
//    fun stopAnimation() {
//        animating = false
//    }
//}
