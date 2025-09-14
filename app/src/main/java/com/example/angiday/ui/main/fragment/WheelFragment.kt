package com.example.angiday.ui.main.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.angiday.R
import kotlin.random.Random

class WheelFragment : Fragment() {

    private lateinit var wheelImage: ImageView
    private lateinit var resultText: TextView
    private var lastDegree = 0f

    private val dishes = listOf(
        "Phở", "Bún bò", "Cơm gà", "Mì xào",
        "Lẩu", "Salad", "Sushi", "Gà rán"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Layout gốc
        val rootLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(0xFFFFF8F5.toInt())
            setPadding(32, 32, 32, 32)
        }

        // ImageView vòng quay
        wheelImage = ImageView(requireContext()).apply {
            setImageResource(R.drawable.ic_wheel) // ảnh vòng quay (vector/png)
            layoutParams = LinearLayout.LayoutParams(600, 600)
        }

        // Text kết quả
        resultText = TextView(requireContext()).apply {
            text = "👉 Bấm nút để quay!"
            textSize = 20f
            setTextColor(0xFF333333.toInt())
            setPadding(0, 32, 0, 32)
        }

        // Nút quay
        val btnSpin = Button(requireContext()).apply {
            text = "🎡 Quay ngay"
            setBackgroundColor(0xFFFF7043.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(32, 16, 32, 16)
        }

        btnSpin.setOnClickListener { spinWheel() }

        // Add vào layout
        rootLayout.addView(wheelImage)
        rootLayout.addView(resultText)
        rootLayout.addView(btnSpin)

        return rootLayout
    }

    private fun spinWheel() {
        val randomDegree = Random.nextInt(360, 360 * 5) // ít nhất 1 vòng
        val finalDegree = lastDegree + randomDegree
        lastDegree = finalDegree % 360

        val rotate = RotateAnimation(
            0f,
            finalDegree.toFloat(),
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 3000
            fillAfter = true
        }

        rotate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                val sector = 360 / dishes.size
                val index = ((360 - lastDegree) / sector).toInt() % dishes.size
                resultText.text = "🍽 Món ăn hôm nay: ${dishes[index]}"
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        wheelImage.startAnimation(rotate)
    }
}
