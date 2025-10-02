package com.example.angiday.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.google.android.material.appbar.MaterialToolbar

data class FoodHistory(
    val name: String,
    val time: String,     // ví dụ: "02/10/2025 11:45"
    val note: String? = null // tùy chọn: "Đã thêm vào yêu thích", "Đã ăn", ...
)

class FoodHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_history)

        // Nút back
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val rv = findViewById<RecyclerView>(R.id.rvHistory)
        val emptyState = findViewById<View>(R.id.emptyState)

        // ====== MẢNG LỊCH SỬ (tĩnh) ======
        val history = listOf(
            FoodHistory("Bún bò Huế", "02/10/2025 11:45", "Đã ăn tại quán A"),
            FoodHistory("Cơm gà xối mỡ", "01/10/2025 19:10", "Đã thêm vào yêu thích"),
            FoodHistory("Sushi cá hồi", "30/09/2025 12:30", "Đặt giao tận nơi"),
            FoodHistory("Phở bò tái chín", "29/09/2025 07:50")
        )

        emptyState.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        rv.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = HistoryAdapter(history)
    }
}

/** Adapter dùng layout hệ thống simple_list_item_2 (2 dòng) */
private class HistoryAdapter(
    private val items: List<FoodHistory>
) : RecyclerView.Adapter<HistoryAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(android.R.id.text1) // tên món
        val subtitle: TextView = view.findViewById(android.R.id.text2) // thời gian + ghi chú
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.name
        holder.subtitle.text = buildString {
            append(item.time)
            if (!item.note.isNullOrBlank()) append(" · ").append(item.note)
        }
    }

    override fun getItemCount(): Int = items.size
}
