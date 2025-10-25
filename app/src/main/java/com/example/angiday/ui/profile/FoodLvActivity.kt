package com.example.angiday.ui.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.session.SessionManager
import com.example.angiday.ui.main.adapter.FoodAdapter
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class FoodLvActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_lv)

        // Nút quay lại
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // RecyclerView setup
        val rv = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvFavoriteFoods)
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = FoodAdapter()
        rv.adapter = adapter

        // Lấy userId từ SessionManager
        val session = SessionManager(this)
        val userId = session.getUserId()
        if (userId == -1L) return

        // Lấy dữ liệu từ Room
        val db = AppDatabase.get(this)
        val foodDao = db.foodDao()
        val behaviorDao = db.userBehaviorDao()
        lifecycleScope.launch {
            val favoriteFoods = behaviorDao.getFavoriteFoodsWithDetail(userId.toInt())
            adapter.submitList(favoriteFoods)
        }

    }
}
