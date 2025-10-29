package com.example.angiday.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserBehaviorEntity
import com.example.angiday.session.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        val tvName = findViewById<TextView>(R.id.tvName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val layoutShared = findViewById<LinearLayout>(R.id.layoutSharedFoods)
        val btnAddPost = findViewById<FloatingActionButton>(R.id.btnAddPost)
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)

        val session = SessionManager(this)
        val userName = session.getUserName()
        val userEmail = session.getUserEmail()
        val userId = session.getUserId()

        tvName.text = userName
        tvEmail.text = userEmail

        topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 🔹 Hiển thị danh sách bài chia sẻ
        fun loadSharedPosts() {
            lifecycleScope.launch {
                val db = AppDatabase.get(this@MyProfileActivity)
                val sharedFoods = db.userBehaviorDao().getSharedFoodsWithDetail(userId.toInt())
                layoutShared.removeAllViews()

                if (sharedFoods.isEmpty()) {
                    val tvEmpty = TextView(this@MyProfileActivity).apply {
                        text = "Chưa có món nào được chia sẻ 🍽️"
                        setTextColor(
                            androidx.core.content.ContextCompat.getColor(
                                this@MyProfileActivity,
                                R.color.colorOnSurface
                            )
                        )
                        textSize = 16f
                        setPadding(20)
                    }
                    layoutShared.addView(tvEmpty)
                } else {
                    sharedFoods.forEach { foodWithRelations ->
                        val itemView =
                            layoutInflater.inflate(R.layout.item_shared_food, layoutShared, false)
                        val img = itemView.findViewById<ImageView>(R.id.imgShared)
                        val title = itemView.findViewById<TextView>(R.id.tvSharedTitle)
                        val desc = itemView.findViewById<TextView>(R.id.tvSharedDesc)
                        val btnDelete = itemView.findViewById<ImageView>(R.id.btnDeletePost)

                        val resId = resources.getIdentifier(
                            foodWithRelations.food.imageRes ?: "",
                            "drawable",
                            packageName
                        )
                        if (resId != 0) img.setImageResource(resId)
                        title.text = foodWithRelations.food.title
                        desc.text = foodWithRelations.food.desc ?: "Không có mô tả"

                        // ❌ Xóa bài chia sẻ
                        btnDelete.setOnClickListener {
                            AlertDialog.Builder(this@MyProfileActivity)
                                .setTitle("Gỡ bài chia sẻ")
                                .setMessage("Bạn có chắc muốn xóa bài chia sẻ này không?")
                                .setPositiveButton("Xóa") { _, _ ->
                                    lifecycleScope.launch {
                                        db.userBehaviorDao().deleteBehavior(
                                            userId = userId.toLong(),
                                            foodWithRelations.food.id,
                                            "shared"
                                        )
                                        loadSharedPosts()
                                        Toast.makeText(
                                            this@MyProfileActivity,
                                            "Đã gỡ bài chia sẻ!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .setNegativeButton("Hủy", null)
                                .show()
                        }

                        layoutShared.addView(itemView)
                    }
                }
            }
        }

        loadSharedPosts()

        // ➕ Tạo bài chia sẻ mới
        btnAddPost.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_share, null)
            val edtName = dialogView.findViewById<EditText>(R.id.edtFoodName)
            val edtDesc = dialogView.findViewById<EditText>(R.id.edtFoodDesc)
            val edtImage = dialogView.findViewById<EditText>(R.id.edtFoodImage)

            AlertDialog.Builder(this)
                .setTitle("Tạo món chia sẻ mới")
                .setView(dialogView)
                .setPositiveButton("Lưu") { _, _ ->
                    val name = edtName.text.toString().ifEmpty { "Món ăn mới" }
                    val desc = edtDesc.text.toString()
                    val image = edtImage.text.toString()

                    lifecycleScope.launch {
                        val db = AppDatabase.get(this@MyProfileActivity)

                        // 🥗 1. Thêm món mới vào bảng FoodEntity
                        val newFood = com.example.angiday.model.entity.FoodEntity(
                            title = name,
                            desc = desc,
                            imageRes = image,
                            instructions = null,
                            youtubeId = null,
                            categoryId = null
                        )
                        val foodId = db.foodDao().insert(newFood)  // trả về id (Long)

                        // 📤 2. Lưu vào bảng UserBehaviorEntity
                        val share = UserBehaviorEntity(
                            userId = userId.toInt(),
                            foodId = foodId.toInt(), // 👈 ép kiểu Long → Int
                            behaviorType = "shared"
                        )

                        db.userBehaviorDao().insert(share)

                        loadSharedPosts()

                        Toast.makeText(
                            this@MyProfileActivity,
                            "Đã chia sẻ món: $name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

    }
}
