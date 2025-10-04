package com.example.angiday.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.angiday.ui.main.MainActivity
import com.example.angiday.R
import com.example.angiday.ui.auth.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class IntroActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabDots: TabLayout
    private lateinit var btnSkip: MaterialButton
    private lateinit var btnNext: MaterialButton

    private val pages = listOf(
        IntroPage(
            title = "Cooking",
            subtitle = "Nhiều món ngon, hãy thử nấu ngay!",
            imageRes = R.drawable.spaghetti
        ),
        IntroPage(
            title = "Gợi ý món ăn",
            subtitle = "Chọn nguyên liệu sẵn có, nhận gợi ý phù hợp.",
            imageRes = R.drawable.ic_home
        ),
        IntroPage(
            title = "Vòng quay may mắn",
            subtitle = "Không biết ăn gì? Xoay vòng chọn món liền!",
            imageRes = R.drawable.ic_home
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewPager = findViewById(R.id.vpIntro)
        tabDots = findViewById(R.id.tabDots)
        btnSkip  = findViewById(R.id.btnSkip)
        btnNext  = findViewById(R.id.btnNext)

        // Adapter
        viewPager.adapter = IntroPagerAdapter(this, pages)

        // Dots indicator
        TabLayoutMediator(tabDots, viewPager) { _, _ -> }.attach()

        // Nút Skip
        btnSkip.setOnClickListener { goToMain() }

        // Nút Next / Bắt đầu
        btnNext.setOnClickListener {
            val lastIndex = (viewPager.adapter?.itemCount ?: 1) - 1
            if (viewPager.currentItem < lastIndex) {
                viewPager.currentItem += 1
            } else {
                goToLogin()
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val last = (viewPager.adapter?.itemCount ?: 1) - 1
                btnNext.text = if (position == last) "Bắt đầu" else "Tiếp tục"
            }
        })
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
