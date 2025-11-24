package com.example.angiday.ui.wheel

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.example.angiday.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SpinWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ========= CONFIG =========
    private val textRadiusPercent = 0.65f       // Vị trí chữ cách tâm bao nhiêu %
    private val textMaxWidthPercent = 0.40f     // Chiều rộng tối đa 1 block chữ theo %
    private val textInnerOffset = 30f           // Đẩy chữ vào trong lát (fix lệch)

    private val spinMinTurns = 4
    private val spinMaxTurns = 7
    private val textSize = 42f

    // ========= STATE =========
    private var wheelItems: List<WheelItem> = emptyList()
    private var currentRotation = 0f
    private var isSpinning = false

    // ========= PAINTS =========
    private val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = this@SpinWheelView.textSize
        textAlign = Paint.Align.CENTER
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

    // Cập nhật danh sách item
    fun setItems(items: List<WheelItem>) {
        wheelItems = items
        invalidate()
    }

    // ========= SPIN LOGIC =========
    fun spin(onFinish: (String) -> Unit) {
        if (isSpinning || wheelItems.isEmpty()) return
        isSpinning = true

        val sectorCount = wheelItems.size
        val sectorAngle = 360f / sectorCount

        val randomSector = Random.nextInt(sectorCount)
        val fullSpins = Random.nextInt(spinMinTurns, spinMaxTurns) * 360f

        // Xoay vào giữa lát
        val targetRotation =
            fullSpins + (360f - (randomSector * sectorAngle + sectorAngle / 2))

        ValueAnimator.ofFloat(currentRotation, currentRotation + targetRotation).apply {
            duration = 4000L
            interpolator = android.view.animation.DecelerateInterpolator()

            addUpdateListener {
                currentRotation = it.animatedValue as Float
                invalidate()
            }

            doOnEnd {
                isSpinning = false

                // Tính kết quả
                val normalized = (360f - (currentRotation % 360f)) % 360f
                val selectedIndex = (normalized / sectorAngle).toInt() % sectorCount
                onFinish(wheelItems[selectedIndex].text)
            }
            start()
        }
    }

    // ========= DRAWING =========
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (wheelItems.isEmpty()) return

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (minOf(width, height) / 2f) * 0.85f

        val sectorAngle = 360f / wheelItems.size
        var startAngle = currentRotation - 90f // Cho 0° ở đỉnh (12 giờ)

        wheelItems.forEach { item ->

            // ===== Vẽ lát =====
            sectorPaint.color = item.color
            canvas.drawArc(
                centerX - radius, centerY - radius,
                centerX + radius, centerY + radius,
                startAngle, sectorAngle,
                true, sectorPaint
            )

            // ===== Vẽ chữ =====

            // Tâm góc lát
            val midAngleDeg = startAngle + sectorAngle / 2f
            val midAngleRad = Math.toRadians(midAngleDeg.toDouble())

            // Tính vị trí chữ
            val textRadius = radius * textRadiusPercent
            val posX = centerX + cos(midAngleRad).toFloat() * textRadius
            val posY = centerY + sin(midAngleRad).toFloat() * textRadius

            // Chiều rộng tối đa để wrap text
            val maxTextWidth = (radius * textMaxWidthPercent).toInt()

            // Build layout
            val layout =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    StaticLayout.Builder.obtain(item.text, 0, item.text.length, textPaint, maxTextWidth)
                        .setAlignment(Layout.Alignment.ALIGN_CENTER)
                        .setIncludePad(false)
                        .build()
                } else {
                    @Suppress("DEPRECATION")
                    StaticLayout(item.text, textPaint, maxTextWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
                }

            // Vẽ chữ
            canvas.save()
            canvas.translate(posX, posY)
            canvas.rotate(midAngleDeg + 90f)      // chữ thẳng đứng
            canvas.translate(textInnerOffset, 0f) // đẩy vào trong để cân giữa lát
            canvas.translate(-layout.width / 2f, -layout.height / 2f)
            layout.draw(canvas)
            canvas.restore()

            startAngle += sectorAngle
        }

        // ===== Vẽ tâm vòng =====
        canvas.drawCircle(centerX, centerY, radius * 0.22f, centerPaint)

        // ===== Vẽ mũi tên chỉ =====
        val arrowLength = radius * 0.15f
        canvas.drawLine(centerX, centerY - radius, centerX, centerY - radius + arrowLength, arrowPaint)

        val path = Path().apply {
            moveTo(centerX - 15f, centerY - radius + arrowLength)
            lineTo(centerX + 15f, centerY - radius + arrowLength)
            lineTo(centerX, centerY - radius + arrowLength - 25f)
            close()
        }
        canvas.drawPath(path, arrowPaint)
    }
}
