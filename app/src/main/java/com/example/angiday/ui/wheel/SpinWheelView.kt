package com.example.angiday.ui.wheel

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.addListener
import androidx.core.graphics.withRotation
import kotlin.math.min
import kotlin.random.Random

data class WheelItem(val label: String, val color: Int)

class SpinWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var items: List<WheelItem> = emptyList()
        set(value) { field = value; invalidate() }

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 36f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.parseColor("#E0E0E0")
    }

    private var wheelRect = RectF()
    private var rotationDeg = 0f
    private var spinAnimator: ValueAnimator? = null

    var onResult: ((index: Int, label: String) -> Unit)? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (items.isEmpty()) return

        val pad = 20f
        wheelRect.set(pad, pad, width - pad, height - pad)
        val sweep = 360f / items.size

        canvas.withRotation(rotationDeg, width / 2f, height / 2f) {
            items.forEachIndexed { i, item ->
                arcPaint.color = item.color
                val start = i * sweep
                drawArc(wheelRect, start, sweep, true, arcPaint)
                drawArc(wheelRect, start, sweep, true, borderPaint)

                // label
                val angle = start + sweep / 2f
                val r = wheelRect.width() / 2.6f
                val cx = width / 2f + r * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
                val cy = height / 2f + r * Math.sin(Math.toRadians(angle.toDouble())).toFloat() + 12f
                textPaint.color = if (isDark(item.color)) Color.WHITE else Color.BLACK
                drawText(item.label, cx, cy, textPaint)
            }
        }

        // mũi tên chỉ định ở đỉnh
        val path = Path().apply {
            val cx = width / 2f
            moveTo(cx - 24f, 8f)
            lineTo(cx + 24f, 8f)
            lineTo(cx, 60f)
            close()
        }
        val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#D32F2F") }
        canvas.drawPath(path, pointerPaint)
    }

    fun spin() {
        if (items.isEmpty()) return
        spinAnimator?.cancel()

        val sweep = 360f / items.size
        val targetIndex = Random.nextInt(items.size)
        val targetAngleCenter = targetIndex * sweep + sweep / 2f
        // cần đưa targetCenter về vị trí mũi tên (góc 0° ở đỉnh),
        // nên quay sao cho rotationDeg % 360 == 360 - targetCenter
        val current = rotationDeg % 360f
        val needed = (360f - targetAngleCenter - current + 360f) % 360f
        val rounds = 5 * 360f
        val finalRotation = rotationDeg + rounds + needed

        spinAnimator = ValueAnimator.ofFloat(rotationDeg, finalRotation).apply {
            duration = 3500
            addUpdateListener {
                rotationDeg = it.animatedValue as Float
                invalidate()
            }
            addListener(onEnd = {
                rotationDeg = finalRotation
                val finalDeg = (rotationDeg % 360f + 360f) % 360f
                val landedIndex = ((360f - finalDeg) / sweep).toInt() % items.size
                val idx = if (landedIndex < 0) landedIndex + items.size else landedIndex
                onResult?.invoke(idx, items[idx].label)
            })
            start()
        }
    }

    private fun isDark(color: Int): Boolean {
        val r = Color.red(color); val g = Color.green(color); val b = Color.blue(color)
        val luminance = 0.299*r + 0.587*g + 0.114*b
        return luminance < 140
    }
}
