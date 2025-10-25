package com.example.angiday.ui.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.session.SessionManager
import com.example.angiday.ui.main.adapter.FoodAdapter
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class FoodHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_history)

        // Nút back
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        // RecyclerView setup
        val rv = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvCookedFoods)
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = FoodAdapter()
        rv.adapter = adapter

        // Lấy userId hiện tại
        val session = SessionManager(this)
        val userId = session.getUserId()
        if (userId == -1L) return

        val db = AppDatabase.get(this)
        val dao = db.userBehaviorDao()

        // Hiển thị món đã nấu
        lifecycleScope.launch {
            val cookedFoods = dao.getCookedFoodsWithDetail(userId.toInt())
            adapter.submitList(cookedFoods)
        }
    }
}
