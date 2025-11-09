package com.example.angiday.ui.wheels

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.*

class SpinWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val sectors = mutableListOf("Phở", "Cơm tấm", "Bún bò", "Mì xào", "Lẩu", "Gà rán")

    private var currentAngle = 0f
    private var highlightIndex = -1

    init {
        paint.style = Paint.Style.FILL
        textPaint.color = Color.WHITE
        textPaint.textSize = 32f
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val radius = min(cx, cy) - 10f
        val sweepAngle = 360f / sectors.size

        canvas.save()
        canvas.rotate(currentAngle, cx, cy)

        for (i in sectors.indices) {
            // Đổi màu highlight khi trúng
            paint.color = when (i) {
                highlightIndex -> Color.parseColor("#FFD700") // vàng sáng
                else -> if (i % 2 == 0) Color.parseColor("#FF6F3C") else Color.parseColor("#FFCA28")
            }

            val start = i * sweepAngle
            canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius, start, sweepAngle, true, paint)

            // Vẽ text
            val angle = Math.toRadians((start + sweepAngle / 2).toDouble())
            val tx = (cx + (radius / 1.5f) * cos(angle)).toFloat()
            val ty = (cy + (radius / 1.5f) * sin(angle)).toFloat() + 10
            canvas.save()
            canvas.rotate(0f, tx, ty)
            canvas.drawText(sectors[i], tx, ty, textPaint)
            canvas.restore()
        }

        canvas.restore()
    }

    /** Xoay bánh ngẫu nhiên và trả về món ăn */
    fun spinRandom(onFinished: (String) -> Unit) {
        val targetIndex = (sectors.indices).random()
        val sweepPerItem = 360f / sectors.size
        val targetAngle = 360f * 5 + (targetIndex * sweepPerItem + sweepPerItem / 2)

        val animator = ValueAnimator.ofFloat(currentAngle, currentAngle + targetAngle)
        animator.duration = 4000
        animator.interpolator = DecelerateInterpolator()

        animator.addUpdateListener {
            currentAngle = it.animatedValue as Float
            invalidate()
        }

        animator.doOnEnd {
            highlightIndex = (sectors.size - (currentAngle / sweepPerItem).roundToInt() % sectors.size) % sectors.size
            invalidate()
            onFinished(sectors[highlightIndex])
        }

        highlightIndex = -1
        animator.start()
    }

    /** Đặt danh sách món ăn mới */
    fun setItems(items: List<String>) {
        sectors.clear()
        sectors.addAll(items)
        highlightIndex = -1
        invalidate()
    }
}
