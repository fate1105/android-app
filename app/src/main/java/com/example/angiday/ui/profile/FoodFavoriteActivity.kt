package com.example.angiday.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.session.SessionManager
import com.example.angiday.ui.main.MainActivity
import com.example.angiday.ui.main.adapter.FoodAdapter
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class FoodFavoriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_lv)

        // Nút quay lại
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // RecyclerView setup
        val rv = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvFavoriteFoods)
        rv.layoutManager = LinearLayoutManager(this)

        // 👉 Truyền callback onClick để mở chi tiết
        val adapter = FoodAdapter(onClick = { foodId ->
            openFoodDetail(foodId)
        })

        rv.adapter = adapter

        // Lấy userId từ SessionManager
        val session = SessionManager(this)
        val userId = session.getUserId()
        if (userId == -1L) return

        // Lấy dữ liệu từ Room
        val db = AppDatabase.get(this)
        val behaviorDao = db.userBehaviorDao()
        lifecycleScope.launch {
            val favoriteFoods = behaviorDao.getFavoriteFoodsWithDetail(userId.toInt())
            adapter.submitList(favoriteFoods)
        }

    }

    // Hàm mở chi tiết
    private fun openFoodDetail(foodId: Long) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("open_food_detail", foodId)
        startActivity(intent)
    }
}
