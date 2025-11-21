package com.example.angiday.ui.wheel

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.example.angiday.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SpinWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var wheelItems: List<WheelItem> = emptyList()
    private var currentRotation = 0f
    private var isSpinning = false

    private val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 42f
        color = Color.WHITE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
    }
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }

    data class WheelItem(val text: String, val color: Int)

    fun setItems(items: List<WheelItem>) {
        wheelItems = items
        invalidate()
    }

    fun spin(onFinish: (String) -> Unit) {
        if (isSpinning || wheelItems.isEmpty()) return
        isSpinning = true

        val sectorCount = wheelItems.size
        val sectorAngle = 360f / sectorCount
        val randomSector = Random.nextInt(sectorCount)
        val fullSpins = Random.nextInt(4, 7) * 360f  // 4-6 vòng cho đã
        val targetRotation = fullSpins + (360f - (randomSector * sectorAngle + sectorAngle / 2))

        ValueAnimator.ofFloat(currentRotation, currentRotation + targetRotation).apply {
            duration = 4000L
            interpolator = android.view.animation.DecelerateInterpolator()
            addUpdateListener {
                currentRotation = it.animatedValue as Float
                invalidate()
            }
            doOnEnd {
                isSpinning = false
                val normalized = (360f - (currentRotation % 360f)) % 360f
                val selectedIndex = (normalized / sectorAngle).toInt() % sectorCount
                val selected = wheelItems[selectedIndex]
                onFinish(selected.text)
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (minOf(width, height) / 2f) * 0.85f  // Để chừa chỗ cho mũi tên

        if (wheelItems.isEmpty()) return

        val sectorAngle = 360f / wheelItems.size
        var startAngle = currentRotation - 90f  // Bắt đầu từ đỉnh (12h)

        wheelItems.forEach { item ->
            sectorPaint.color = item.color
            canvas.drawArc(
                centerX - radius, centerY - radius,
                centerX + radius, centerY + radius,
                startAngle, sectorAngle, true, sectorPaint
            )

            // Text
            val textAngleRad = Math.toRadians((startAngle + sectorAngle / 2).toDouble())
            val textRadius = radius * 0.65f
            val textX = centerX + (cos(textAngleRad) * textRadius).toFloat()
            val textY = centerY + (sin(textAngleRad) * textRadius).toFloat() + 15f  // offset cho baseline

            // Xoay text theo sector (đẹp hơn)
            canvas.save()
            canvas.translate(textX, textY)
            canvas.rotate(startAngle + sectorAngle / 2 + 90f)
            canvas.drawText(item.text, 0f, 0f, textPaint)
            canvas.restore()

            startAngle += sectorAngle
        }

        // Vòng tròn giữa
        canvas.drawCircle(centerX, centerY, radius * 0.22f, centerPaint)

        // Mũi tên chỉ trên cùng (cố định)
        val arrowLength = radius * 0.15f
        canvas.drawLine(centerX, centerY - radius, centerX, centerY - radius + arrowLength, arrowPaint)
        // Tam giác mũi tên
        val path = Path().apply {
            moveTo(centerX - 15f, centerY - radius + arrowLength)
            lineTo(centerX + 15f, centerY - radius + arrowLength)
            lineTo(centerX, centerY - radius + arrowLength - 25f)
            close()
        }
        canvas.drawPath(path, arrowPaint)
    }
}