package com.example.angiday.ui.share

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.angiday.R

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        val tvShare = findViewById<TextView>(R.id.tvShare)
        val btnClose = findViewById<Button>(R.id.btnClose)

        // 👉 Nhận nội dung chia sẻ từ Intent
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        tvShare.text = if (!sharedText.isNullOrEmpty()) {
            "📋 Nội dung được chia sẻ:\n\n$sharedText"
        } else {
            "⚠️ Không có nội dung nào được chia sẻ!"
        }

        // 👉 Nút đóng Activity
        btnClose.setOnClickListener {
            finish()
        }
    }
}
