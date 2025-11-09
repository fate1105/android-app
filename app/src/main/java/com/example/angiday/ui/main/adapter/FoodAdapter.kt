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
    private val onClick: (Long) -> Unit = {} // callback truyền foodId
) : RecyclerView.Adapter<FoodAdapter.VH>() {

    // ViewHolder ánh xạ layout item_food.xml
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgFood)
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val desc: TextView = itemView.findViewById(R.id.tvDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position].food

        // hiển thị thông tin món ăn
        holder.title.text = item.title
        holder.desc.text = item.desc ?: "Không có mô tả"

        // load ảnh từ tên resource qua ImageUtils
        val resId = ImageUtils.getDrawableId(holder.itemView.context, item.imageRes)
        holder.img.setImageResource(if (resId != 0) resId else R.drawable.ic_launcher_foreground)

        // xử lý khi click vào món ăn
        holder.itemView.setOnClickListener {
            Log.d("FoodAdapter", "Clicked food: ${item.title}, id=${item.id}")
            onClick(item.id)
        }
    }

    override fun getItemCount(): Int = items.size // số lượng món

    // cập nhật danh sách món ăn
    fun submitList(newItems: List<FoodWithRelations>) {
        items = newItems
        notifyDataSetChanged()
    }
}
