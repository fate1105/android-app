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

data class CookingSeries(
    val title: String,   // tên chuỗi/bữa (ví dụ: “Meal Prep tuần 40”)
    val time: String,    // thời gian (ví dụ: “02–06/10/2025 · Tối 18:00”)
    val note: String? = null // ghi chú (ví dụ: “5 món / 3 ngày / low-carb”)
)

class CookingSeriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cooking_series)

        // Back arrow
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val rv = findViewById<RecyclerView>(R.id.rvSeries)
        val emptyState = findViewById<View>(R.id.emptyState)

        // ===== DỮ LIỆU TĨNH (mảng chuỗi nấu ăn) =====
        val series = listOf(
            CookingSeries("Meal Prep tuần 40", "02–06/10/2025 · Tối 18:00", "5 món / 3 ngày / low-carb"),
            CookingSeries("Chuỗi cơm gia đình", "03/10/2025 · 11:30", "Cơm gà, canh bí đỏ, rau luộc"),
            CookingSeries("Set healthy trưa", "01–03/10/2025 · 12:00", "Salad + ức gà áp chảo + soup"),
            CookingSeries("Cuối tuần BBQ", "05/10/2025 · 18:30", "Thịt nướng + rau củ + nước chấm")
        )

        emptyState.visibility = if (series.isEmpty()) View.VISIBLE else View.GONE
        rv.visibility = if (series.isEmpty()) View.GONE else View.VISIBLE

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = SeriesAdapter(series)
    }
}

/** Adapter dùng sẵn layout hệ thống 2 dòng (simple_list_item_2) */
private class SeriesAdapter(
    private val items: List<CookingSeries>
) : RecyclerView.Adapter<SeriesAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(android.R.id.text1) // dòng 1
        val subtitle: TextView = view.findViewById(android.R.id.text2) // dòng 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.subtitle.text = buildString {
            append(item.time)
            if (!item.note.isNullOrBlank()) append(" · ").append(item.note)
        }
    }

    override fun getItemCount(): Int = items.size
}
