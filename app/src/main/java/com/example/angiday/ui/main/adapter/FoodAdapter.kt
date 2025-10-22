package com.example.angiday.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.model.relations.FoodWithRelations

import android.content.Intent
import android.widget.Button

import com.example.angiday.util.ImageUtils

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

        // 🔹 Load ảnh theo tên trong drawable
        val resId = if (!item.imageRes.isNullOrEmpty()) {
            holder.itemView.context.resources.getIdentifier(
                item.imageRes,
                "drawable",
                holder.itemView.context.packageName
            )
        } else 0

        if (resId != 0) {
            holder.img.setImageResource(resId)
        } else {
            holder.img.setImageResource(R.drawable.ic_launcher_foreground)
        }

        val resId = ImageUtils.getDrawableId(holder.itemView.context, item.imageRes)
        holder.img.setImageResource(resId)


        // 🔹 Click vào toàn bộ item (điều hướng sang chi tiết)
        holder.itemView.setOnClickListener { onClick(item.id) }

        // 🔹 Nút "Chia sẻ món ăn" (Implicit Intent)
        // 🔹 Nút "Chia sẻ món ăn" (Implicit Intent)
        val btnShare = holder.itemView.findViewById<Button>(R.id.btnShare)
        btnShare.setOnClickListener {
            val name = holder.title.text.toString()
            val desc = holder.desc.text.toString()

            val intent = Intent(Intent.ACTION_SEND).apply {
                this.type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Món ăn ngon hôm nay!")
                putExtra(Intent.EXTRA_TEXT, "Hôm nay bạn nên thử món: $name\n$desc 🍽️")
            }
            holder.itemView.context.startActivity(
                Intent.createChooser(intent, "Chia sẻ món ăn qua...")
            )
        }

    }


    override fun getItemCount() = items.size

    fun submitList(newItems: List<FoodWithRelations>) {
        items = newItems
        notifyDataSetChanged()
    }
}
