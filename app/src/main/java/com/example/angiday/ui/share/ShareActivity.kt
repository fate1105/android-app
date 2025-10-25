package com.example.angiday.ui.share

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserBehaviorEntity
import com.example.angiday.session.SessionManager
import kotlinx.coroutines.launch

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        val tvShare = findViewById<TextView>(R.id.tvShare)
        val imgFood = findViewById<ImageView>(R.id.imgFoodShare)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmShare)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        val sharedText = intent.getStringExtra(android.content.Intent.EXTRA_TEXT)
        val imageUriString = intent.getStringExtra(android.content.Intent.EXTRA_STREAM)
        val imageUri = imageUriString?.let { Uri.parse(it) }

        tvShare.text = if (!sharedText.isNullOrEmpty()) {
            "📋 Nội dung được chia sẻ:\n\n$sharedText"
        } else {
            "⚠️ Không có nội dung nào được chia sẻ!"
        }

        if (imageUri != null) {
            imgFood.setImageURI(imageUri)
        }

        // ✅ Nút xác nhận chia sẻ
        btnConfirm.setOnClickListener {
            val session = SessionManager(this)
            val userId = session.getUserId()
            val foodId = intent.getLongExtra("food_id", -1L)

            lifecycleScope.launch {
                val dao = AppDatabase.get(this@ShareActivity).userBehaviorDao()
                dao.insert(
                    UserBehaviorEntity(
                        userId = userId.toInt(),
                        foodId = foodId.toInt(),
                        behaviorType = "shared"
                    )
                )
            }

            // 👉 Sau khi xác nhận -> về trang cá nhân
            finish() // hoặc mở ProfileFragment
        }

        // ❌ Nút đóng (hủy chia sẻ)
        btnCancel.setOnClickListener {
            finish()
        }
    }
}
