package com.example.angiday.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R

class SuggestionAdapter(
    private var items: MutableList<String>,
    private val onClick: (String) -> Unit // callback khi bấm vào item
) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    // ViewHolder chứa TextView hiển thị gợi ý
    inner class ViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(item: String) {
            textView.text = item
            textView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflate layout item_suggestion.xml
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        val textView = view.findViewById<TextView>(R.id.tvSuggest)
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position]) // bind dữ liệu vào item
    }

    override fun getItemCount(): Int = items.size // số lượng item

    // thêm 1 gợi ý mới lên đầu danh sách
    fun addNewSuggestion(food: String) {
        items.add(0, food)
        notifyItemInserted(0)
    }

    // cập nhật toàn bộ danh sách gợi ý
    fun updateData(newItems: List<String>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
