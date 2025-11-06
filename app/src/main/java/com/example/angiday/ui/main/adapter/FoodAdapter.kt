package com.example.angiday.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.model.relations.FoodWithRelations
import com.example.angiday.utils.ImageUtils

class FoodAdapter(
    private var items: List<FoodWithRelations> = emptyList(),
    private val onClick: (Long) -> Unit = {}   // truyền foodId
) : RecyclerView.Adapter<FoodAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgFood)
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val desc: TextView = itemView.findViewById(R.id.tvDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position].food
        holder.title.text = item.title
        holder.desc.text = item.desc ?: "Không có mô tả"

        // 🔹 Load ảnh bằng ImageUtils
        val resId = ImageUtils.getDrawableId(holder.itemView.context, item.imageRes)
        if (resId != 0) {
            holder.img.setImageResource(resId)
        } else {
            holder.img.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // 🔹 Click vào item
        holder.itemView.setOnClickListener {     Log.d("DEBUG_CLICK", "Clicked food: ${item.title}, id=${item.id}")
            onClick(item.id)
        }

        // 🔹 Nút "Chia sẻ món ăn"

    }


    override fun getItemCount() = items.size

    fun submitList(newItems: List<FoodWithRelations>) {
        items = newItems
        notifyDataSetChanged()
    }
}
