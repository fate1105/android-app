package com.example.angiday.ui.main.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.relations.FoodWithRelations
import com.example.angiday.repository.FoodRepository
import com.example.angiday.viewmodel.FoodDetailViewModel
import com.example.angiday.viewmodel.FoodDetailViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FoodDetailFragment : Fragment() {
    companion object {
        private const val ARG_FOOD_ID = "arg_food_id"
        fun newInstance(foodId: Long) = FoodDetailFragment().apply {
            arguments = Bundle().apply { putLong(ARG_FOOD_ID, foodId) }
        }
    }
    private lateinit var img: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDesc: TextView
    private lateinit var chipGroupIngredients: ChipGroup
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var tvCategory: TextView
    private lateinit var tvInstructions: TextView
    private lateinit var youtubeContainer: LinearLayout

    private val viewModel: FoodDetailViewModel by viewModels {
        val dao = AppDatabase.get(requireContext()).foodDao()
        FoodDetailViewModelFactory(FoodRepository(dao))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_food_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        img = view.findViewById(R.id.imgFoodDetail)
        tvTitle = view.findViewById(R.id.tvFoodTitle)
        tvDesc = view.findViewById(R.id.tvFoodDesc)
        chipGroupIngredients = view.findViewById(R.id.chipGroupIngredients)
        chipGroupTags = view.findViewById(R.id.chipGroupTags)
        tvCategory = view.findViewById(R.id.tvCategory)
        tvInstructions = view.findViewById(R.id.tvInstructions)
        youtubeContainer = view.findViewById(R.id.youtubeContainer)

        val foodId = requireArguments().getLong(ARG_FOOD_ID)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFood(foodId).collectLatest { foodWithRelations ->
                foodWithRelations?.let { bindFood(it) }
            }
        }
    }

    private fun bindFood(food: FoodWithRelations) {
        val foodEntity = food.food

        tvTitle.text = foodEntity.title
        tvDesc.text = foodEntity.desc ?: "Không có mô tả"

        // Ảnh
        val resId = foodEntity.imageRes?.let { name ->
            resources.getIdentifier(name, "drawable", requireContext().packageName)
        } ?: 0

        if (resId != 0) img.setImageResource(resId)
        else img.setImageResource(R.drawable.ic_launcher_foreground)

        // Ingredients
        chipGroupIngredients.removeAllViews()
        food.ingredients.forEach { ing ->
            val chip = Chip(requireContext()).apply {
                text = ing.name
                isCheckable = false
                isClickable = false
            }
            chipGroupIngredients.addView(chip)
        }

        // Tags
        chipGroupTags.removeAllViews()
        food.tags.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag.name
                isCheckable = false
                isClickable = false
            }
            chipGroupTags.addView(chip)
        }

        // Category
        tvCategory.text = food.category?.name ?: "Không có danh mục"

        // Instructions
        tvInstructions.text = foodEntity.instructions ?: "Chưa có hướng dẫn nấu chi tiết."

        // YouTube
        if (!foodEntity.youtubeId.isNullOrBlank()) {
            youtubeContainer.visibility = View.VISIBLE
            youtubeContainer.setOnClickListener {
                openYoutubeVideo(foodEntity.youtubeId!!)
            }
        } else {
            youtubeContainer.visibility = View.GONE
        }
    }

    private fun openYoutubeVideo(videoId: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
        startActivity(intent)
    }
}
