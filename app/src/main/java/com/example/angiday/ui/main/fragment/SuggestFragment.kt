package com.example.angiday.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.google.android.material.textview.MaterialTextView

class SuggestFragment : Fragment() {

    private lateinit var rvRecipes: RecyclerView
    private lateinit var tvSelectedIngredients: MaterialTextView

    private val demoRecipes = listOf(
        Recipe("Salad cá hồi", listOf("Cá hồi", "Cà chua", "Rau xanh")),
        Recipe("Phở bò tái", listOf("Phở bò", "Hành", "Rau thơm")),
        Recipe("Cơm chiên Dương Châu", listOf("Cơm chiên", "Cà rốt", "Đậu Hà Lan")),
        Recipe("Gà chiên nước mắm", listOf("Cánh gà", "Tỏi", "Ớt")),
        Recipe("Mì xào hải sản", listOf("Mì", "Tôm", "Mực", "Rau cải"))
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_suggest, container, false)
        rvRecipes = view.findViewById(R.id.rvRecipes)
        tvSelectedIngredients = view.findViewById(R.id.tvSelectedIngredients)

        val ingredients = arguments?.getStringArray("ingredients")?.toList() ?: emptyList()

        // Hiện nguyên liệu đã chọn
        tvSelectedIngredients.text =
            if (ingredients.isNotEmpty()) {
                "Nguyên liệu đã chọn: ${ingredients.joinToString(", ")}"
            } else {
                "Chưa chọn nguyên liệu nào"
            }

        // (demo) hiển thị full list, có thể sau này đổi thành lọc theo ingredients
        rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        rvRecipes.adapter = RecipeAdapter(demoRecipes)

        return view
    }
}


data class Recipe(val name: String, val ingredients: List<String>)

class RecipeAdapter(private val recipes: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFood: ImageView = view.findViewById(R.id.imgFood)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false) // item_recipe là file CardView bạn gửi
        return ViewHolder(view)
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.tvTitle.text = recipe.name
        holder.tvDesc.text = "Nguyên liệu: ${recipe.ingredients.joinToString(", ")}"

        // (Demo) Set ảnh tạm, sau này bạn có thể đổi thành Glide/Picasso khi có link ảnh
        holder.imgFood.setImageResource(R.drawable.ic_launcher_foreground)
    }
}
