package com.example.angiday.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R

class ShoppingAdapter(
    private val items: MutableList<String>,
    private val onChecked: (String) -> Unit
) : RecyclerView.Adapter<ShoppingAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val cb: CheckBox = view.findViewById(R.id.cbItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.cb.text = item

        holder.cb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) onChecked(item)
        }
    }

    override fun getItemCount() = items.size
}
