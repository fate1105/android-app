package com.example.angiday.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.model.relations.FoodWithRelations

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
        holder.desc.text = item.desc

        if (item.imageRes != null) {
            holder.img.setImageResource(item.imageRes)
        } else {
            holder.img.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // click vào toàn bộ item (chứ không chỉ ảnh)
        holder.itemView.setOnClickListener { onClick(item.id) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<FoodWithRelations>) {
        items = newItems
        notifyDataSetChanged()
    }
}
