package com.example.launcher.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class MatrixBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val charset = ("01abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "あいうえおカキクケコサシスセソ").toCharArray()

    private val headPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(255, 180, 255, 160)
        style = Paint.Style.FILL
        isFakeBoldText = true
    }

    private val trailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 0, 255, 70)
        style = Paint.Style.FILL
    }

    private val fadePaint = Paint().apply {
        // Adjust alpha to control how long trails persist
        color = Color.argb(35, 0, 0, 0)
    }

    // Grid setup
    private var charSize = 0f
    private var charWidth = 0f
    private var charHeight = 0f
    private var cols = 0
    private var rows = 0

    private lateinit var yPositions: FloatArray
    private lateinit var speeds: FloatArray
    private lateinit var buffers: Array<CharArray>

    // Rain tuning
    private val trailLength = 25
    private val minSpeed = 6f
    private val maxSpeed = 20f

    private var running = false

    init {
        val density = context.resources.displayMetrics.density
        // Smaller chars for denser look
        charSize = 12f * density
        headPaint.textSize = charSize
        trailPaint.textSize = charSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        charWidth = trailPaint.measureText("あ") * 0.8f
        charHeight = charSize * 0.95f

        // More columns for denser rain
        cols = (w / charWidth).toInt().coerceAtLeast(3)
        rows = (h / charHeight).toInt().coerceAtLeast(10)

        yPositions = FloatArray(cols)
        speeds = FloatArray(cols)
        buffers = Array(cols) { CharArray(rows) }

        for (c in 0 until cols) {
            yPositions[c] = Random.nextFloat() * height
            speeds[c] = minSpeed + Random.nextFloat() * (maxSpeed - minSpeed)
            for (r in 0 until rows) {
                buffers[c][r] = randomChar()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw fading background (creates the trail)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), fadePaint)

        for (c in 0 until cols) {
            val x = c * charWidth
            var y = yPositions[c]

            for (t in 0 until trailLength) {
                val index = (y / charHeight - t).toInt().mod(rows)
                if (index < 0 || index >= rows) continue

                val paint = if (t == 0) headPaint else trailPaint
                val alpha = (255 * (1f - t.toFloat() / trailLength)).toInt().coerceIn(25, 255)
                paint.alpha = alpha

                canvas.drawText(buffers[c][index].toString(), x, y - t * charHeight, paint)
            }

            // Move rain down
            yPositions[c] += speeds[c]
            if (yPositions[c] > height + trailLength * charHeight) {
                yPositions[c] = -Random.nextFloat() * height / 2
                speeds[c] = minSpeed + Random.nextFloat() * (maxSpeed - minSpeed)
                for (r in 0 until rows) {
                    buffers[c][r] = randomChar()
                }
            }

            // Occasionally change characters mid-fall for more life
            if (Random.nextFloat() < 0.08f) {
                val index = Random.nextInt(rows)
                buffers[c][index] = randomChar()
            }
        }

        if (running) postInvalidateOnAnimation()
    }

    fun startAnimation() {
        if (!running) {
            running = true
            invalidate()
        }
    }

    fun stopAnimation() {
        running = false
    }

    private fun randomChar(): Char = charset[Random.nextInt(charset.size)]
}











//
//
//
//
////*********
//package com.example.launcher.widgets
//
//import android.content.Context
//import android.graphics.*
//import android.util.AttributeSet
//import android.view.View
//import kotlin.math.cos
//import kotlin.math.sin
//import kotlin.random.Random
//
//class MatrixBackgroundView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//
//    private val bgPaint = Paint().apply {
//        color = Color.BLACK
//    }
//
//    private val glowPaint = Paint().apply {
//        color = Color.parseColor("#00FF00")
//        style = Paint.Style.FILL
//        isAntiAlias = true
//        maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)
//    }
//
//    private val linePaint = Paint().apply {
//        color = Color.parseColor("#004400")
//        strokeWidth = 1.2f
//        isAntiAlias = true
//    }
//
//    private val particleCount = 70
//    private val particles = mutableListOf<Particle>()
//    private var animating = false
//
//    private data class Particle(
//        var x: Float,
//        var y: Float,
//        var r: Float,
//        var vx: Float,
//        var vy: Float,
//        var alpha: Int
//    )
//
//    init {
//        for (i in 0 until particleCount) {
//            particles.add(
//                Particle(
//                    x = Random.nextFloat() * 1080,
//                    y = Random.nextFloat() * 1920,
//                    r = Random.nextFloat() * 4f + 2f,
//                    vx = Random.nextFloat() * 2f - 1f,
//                    vy = Random.nextFloat() * 2f - 1f,
//                    alpha = Random.nextInt(100, 255)
//                )
//            )
//        }
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        // Draw dark fading background to create trail effect
//        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
//
//        // Subtle digital grid
//        val gridSpacing = 60
//        for (x in 0 until width step gridSpacing) {
//            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), linePaint)
//        }
//        for (y in 0 until height step gridSpacing) {
//            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), linePaint)
//        }
//
//        // Animate glowing particles
//        for (p in particles) {
//            glowPaint.alpha = p.alpha
//            canvas.drawCircle(p.x, p.y, p.r, glowPaint)
//
//            // slight drift in movement
//            p.x += p.vx
//            p.y += p.vy + sin(p.x / 80f) * 0.4f
//
//            // wrap around screen edges
//            if (p.x < 0) p.x = width.toFloat()
//            if (p.x > width) p.x = 0f
//            if (p.y < 0) p.y = height.toFloat()
//            if (p.y > height) p.y = 0f
//        }
//
//        if (animating) postInvalidateOnAnimation()
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
//





















//
//
//
//
//package com.example.launcher.widgets
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.util.AttributeSet
//import android.view.View
//import kotlin.random.Random
//import androidx.core.graphics.toColorInt
//
//class MatrixBackgroundView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//
//    private val textSize = 20f
//
//    private val paint = Paint().apply {
//        color = "green".toColorInt()
//        textSize = this@MatrixBackgroundView.textSize
//        typeface = android.graphics.Typeface.MONOSPACE
//        isAntiAlias = true
//    }
//
////    test
//
//    private var numRows = 0
//    private var numCols = 0
//    private lateinit var matrixChars: Array<CharArray>
//    private lateinit var xOffsets: FloatArray
//    private lateinit var yPositions: FloatArray
//
//    private val speed = 10f
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
//
//        // Fill random chars
//        for (row in 0 until numRows) {
//            for (col in 0 until numCols) {
//                matrixChars[row][col] = if (Random.nextBoolean()) '0' else '1'
//            }
//        }
//
//        // lateral offsets
//        xOffsets = FloatArray(numRows) { 0f }
//
//        // y positions (start at top, row by row downward)
//        yPositions = FloatArray(numRows) { row ->
//            (row + 1) * charHeight
//        }
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        val charWidth = paint.measureText("0")
//
//        for (row in 0 until numRows) {
//            val offset = xOffsets[row]
//
//            for (col in 0 until numCols) {
//                // pick a char dynamically, not static
//                val char = if (Random.nextBoolean()) '0' else '1'
//
//                var x = (col * charWidth - offset)
//                if (x < -charWidth) {
//                    x += numCols * charWidth
//                }
//                canvas.drawText(char.toString(), x, yPositions[row], paint)
//            }
//
//            // move this row independently
//            xOffsets[row] += speed
//            if (xOffsets[row] > charWidth * numCols) {
//                xOffsets[row] = 0f
//            }
//        }
//
//        if (animating) {
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
//







//
//package com.example.launcher.widgets
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.util.AttributeSet
//import android.view.View
//import kotlin.random.Random
//import androidx.core.graphics.toColorInt
//
//class MatrixBackgroundView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//
//    private val textSize = 20f
//
//    private val paint = Paint().apply {
//        textSize = this@MatrixBackgroundView.textSize
//        typeface = android.graphics.Typeface.MONOSPACE
//        isAntiAlias = true
//    }
//    private val colors = listOf(
//        "#00FF00", // bright green
//        "#00FFFF", // bright cyan
//        "#FFFF00", // bright yellow
//        "#FF00FF", // bright magenta
//        "#FF4500", // orange-red for extra pop
//        "#00BFFF"  // deep sky blue
//    ).map { it.toColorInt() }
//
//    private var numRows = 0
//    private var numCols = 0
//    private lateinit var matrixChars: Array<CharArray>
//    private lateinit var xOffsets: FloatArray
//    private lateinit var yPositions: FloatArray
//
//    private val speed = 10f
//    private var animating = false
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        val charWidth = paint.measureText("0")
//        val charHeight = paint.textSize
//
//        numCols = (w / charWidth).toInt()
//        numRows = (h / charHeight).toInt()
//
//        matrixChars = Array(numRows) { CharArray(numCols) { if (Random.nextBoolean()) '0' else '1' } }
//
//        xOffsets = FloatArray(numRows) { 0f }
//        yPositions = FloatArray(numRows) { row -> (row + 1) * charHeight }
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        val charWidth = paint.measureText("0")
//
//        for (row in 0 until numRows) {
//            val offset = xOffsets[row]
//
//            for (col in 0 until numCols) {
//                // Pick a char dynamically
//                val char = if (Random.nextBoolean()) '0' else '1'
//
//                // Pick a random color from the palette
//                paint.color = colors[Random.nextInt(colors.size)]
//
//                var x = (col * charWidth - offset)
//                if (x < -charWidth) x += numCols * charWidth
//
//                canvas.drawText(char.toString(), x, yPositions[row], paint)
//            }
//
//            // move this row independently
//            xOffsets[row] += speed
//            if (xOffsets[row] > charWidth * numCols) xOffsets[row] = 0f
//        }
//
//        if (animating) postInvalidateOnAnimation()
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




