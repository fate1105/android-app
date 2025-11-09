package com.example.angiday.ui.wheels

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.example.angiday.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class WheelBottomSheet(
    private val title: String,
    private val items: List<WheelItem>,
    private val onResult: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var wheelView: WheelView
    private lateinit var tvTitle: TextView
    private lateinit var tvResult: TextView
    private var spinning = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottomsheet_wheel, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        wheelView = view.findViewById(R.id.wheelView)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvResult = view.findViewById(R.id.tvResult)

        tvTitle.text = title
        wheelView.setItems(items)

        view.findViewById<View>(R.id.btnSpin).setOnClickListener {
            if (!spinning) spinWheel()
        }
    }

    private fun spinWheel() {
        val target = Random.nextInt(items.size)
        spinning = true

        val rotationTarget = 360f * 5 + (360f / items.size) * target
        val animator = ValueAnimator.ofFloat(wheelView.rotation, wheelView.rotation + rotationTarget)
        animator.duration = 3000
        animator.addUpdateListener { wheelView.rotation = it.animatedValue as Float }

        animator.doOnEnd {
            val picked = items[target].label
            tvResult.text = "🍽️ $picked"
            spinning = false
            onResult(picked)
        }
        animator.start()
    }
}

/**
 * Custom View vẽ vòng quay nhiều màu.
 */
class WheelView(context: android.content.Context, attrs: android.util.AttributeSet? = null) :
    View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var wheelItems: List<WheelItem> = emptyList()

    init {
        textPaint.textSize = 36f
        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER
    }

    fun setItems(items: List<WheelItem>) {
        wheelItems = items
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (wheelItems.isEmpty()) return

        val cx = width / 2f
        val cy = height / 2f
        val radius = min(cx, cy) - 10f
        val sweep = 360f / wheelItems.size

        for (i in wheelItems.indices) {
            paint.color = wheelItems[i].color
            val start = i * sweep
            canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius, start, sweep, true, paint)

            val angle = Math.toRadians((start + sweep / 2).toDouble())
            val tx = (cx + (radius / 1.5f) * cos(angle)).toFloat()
            val ty = (cy + (radius / 1.5f) * sin(angle)).toFloat() + 10
            canvas.drawText(wheelItems[i].label, tx, ty, textPaint)
        }

        // Vẽ mũi tên chỉ
        paint.color = Color.BLACK
        val path = Path().apply {
            moveTo(cx, cy - radius - 20)
            lineTo(cx - 20, cy - radius + 30)
            lineTo(cx + 20, cy - radius + 30)
            close()
        }
        canvas.drawPath(path, paint)
    }
}
