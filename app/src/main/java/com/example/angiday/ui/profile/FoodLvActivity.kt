package com.example.angiday.ui.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.ui.main.adapter.FoodAdapter
//import com.example.angiday.model.Food
import com.google.android.material.appbar.MaterialToolbar

class FoodLvActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_lv)

        // Nút back trên AppBar
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val rv = findViewById<RecyclerView>(R.id.rvFavorites)

//        val foods = listOf(
//            Food("Phở bò", "Nước dùng đậm, bò tái.", R.drawable.logo),
//            Food("Bún bò Huế", "Cay nhẹ, thơm sả.", R.drawable.logo),
//            Food("Cơm tấm", "Sườn bì chả.", R.drawable.logo),
//            Food("Bánh mì", "Pate, dưa leo.", R.drawable.logo)
//        )
//
//        rv.layoutManager = LinearLayoutManager(applicationContext)
//        rv.setHasFixedSize(true)
//        rv.adapter = FoodAdapter(foods) { /* handle click nếu cần */ }


    }
}
