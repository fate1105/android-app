package com.example.angiday.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.model.relations.FoodWithRelations
class MenuAdapter(
    private var items: List<FoodWithRelations>
) : RecyclerView.Adapter<MenuAdapter.FoodVH>() {

    inner class FoodVH(view: View) : RecyclerView.ViewHolder(view) {
//        val imgFood: ImageView = view.findViewById(R.id.imgFood)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodVH(v)
    }

    override fun onBindViewHolder(holder: FoodVH, position: Int) {
        val food = items[position]
        holder.tvTitle.text = food.food.title
        holder.tvDesc.text = food.food.desc
        // Nếu bạn có cột imagePath trong DB:
        // holder.imgFood.setImageResource(...) hoặc dùng Glide/Picasso để load ảnh.
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newList: List<FoodWithRelations>) {
        items = newList
        notifyDataSetChanged()
    }
}
//