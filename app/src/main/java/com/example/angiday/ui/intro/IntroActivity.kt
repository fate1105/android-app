package com.example.angiday.ui.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.angiday.R
import com.example.angiday.ui.auth.LoginActivity

class IntroActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var startBtn: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Khai báo nút
        startBtn = findViewById(R.id.startBtn)
        startBtn.setOnClickListener(this)
    }
    // Xử lý sự kiện click
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.startBtn -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }
}
