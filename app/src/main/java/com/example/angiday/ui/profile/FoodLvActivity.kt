package com.example.angiday.ui.profile

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
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
        val emptyState = findViewById<View>(R.id.emptyState)

        // ======= MẢNG MÓN ĂN (tĩnh) =======
        val foods = listOf(
            "Cơm gà xối mỡ",
            "Bún bò Huế",
            "Phở bò tái chín",
            "Bánh mì thịt nướng",
            "Sushi cá hồi",
            "Pizza hải sản",
            "Gà rán KFC",
            "Lẩu Thái chua cay"
        )

        // Hiển thị/ẩn empty state
        emptyState.visibility = if (foods.isEmpty()) View.VISIBLE else View.GONE
        rv.visibility = if (foods.isEmpty()) View.GONE else View.VISIBLE

        // Gắn RecyclerView
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = FoodTextAdapter(foods) { name ->
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show() // click item (tuỳ chọn)
        }
    }
}

/** Adapter cực gọn: mỗi item chỉ là một TextView */
private class FoodTextAdapter(
    private val items: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<FoodTextAdapter.VH>() {

    class VH(val tv: TextView) : RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val tv = TextView(parent.context).apply {
            textSize = 18f
            // padding theo dp
            val dp = (parent.context.resources.displayMetrics.density)
            setPadding((16*dp).toInt(), (14*dp).toInt(), (16*dp).toInt(), (14*dp).toInt())
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return VH(tv)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val name = items[position]
        holder.tv.text = name
        holder.itemView.setOnClickListener { onClick(name) }
    }

    override fun getItemCount(): Int = items.size
}
