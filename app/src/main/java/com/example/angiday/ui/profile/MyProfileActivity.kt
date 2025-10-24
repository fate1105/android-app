package com.example.angiday.ui.profile

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.session.SessionManager
import kotlinx.coroutines.launch
import com.google.android.material.appbar.MaterialToolbar
class MyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        val tvName = findViewById<TextView>(R.id.tvName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val layoutShared = findViewById<LinearLayout>(R.id.layoutSharedFoods)

        // 👉 Lấy thông tin user từ SessionManager
        val session = SessionManager(this)
        val userName = session.getUserName()
        val userEmail = session.getUserEmail()
        val userId = session.getUserId()

        tvName.text = userName
        tvEmail.text = userEmail

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()  // Quay lại màn trước
        }
        // 👉 Lấy danh sách món đã chia sẻ
        lifecycleScope.launch {
            val db = AppDatabase.get(this@MyProfileActivity)
            val sharedFoods = db.userBehaviorDao().getSharedFoodsWithDetail(userId.toInt())

            layoutShared.removeAllViews()
            if (sharedFoods.isEmpty()) {
                val tvEmpty = TextView(this@MyProfileActivity).apply {
                    text = "Chưa có món nào được chia sẻ 🍽️"
                    setTextColor(androidx.core.content.ContextCompat.getColor(this@MyProfileActivity, R.color.colorOnSurface))

                    textSize = 16f
                    setPadding(20)
                }
                layoutShared.addView(tvEmpty)
            } else {
                sharedFoods.forEach { foodWithRelations ->
                    val itemView = layoutInflater.inflate(R.layout.item_shared_food, layoutShared, false)
                    val img = itemView.findViewById<ImageView>(R.id.imgShared)
                    val title = itemView.findViewById<TextView>(R.id.tvSharedTitle)
                    val desc = itemView.findViewById<TextView>(R.id.tvSharedDesc)

                    val resId = resources.getIdentifier(
                        foodWithRelations.food.imageRes ?: "",
                        "drawable",
                        packageName
                    )
                    if (resId != 0) img.setImageResource(resId)
                    title.text = foodWithRelations.food.title
                    desc.text = foodWithRelations.food.desc ?: "Không có mô tả"

                    layoutShared.addView(itemView)
                }
            }
        }
    }
}
