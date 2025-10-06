package com.example.angiday.ui.main.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
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

    private var _webView: WebView? = null
    private val webView get() = _webView!!

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
        chipGroupTags = view.findViewById(R.id.chipGroupTags) // tạo thêm chip group cho tags
        tvCategory = view.findViewById(R.id.tvCategory)       // TextView hiển thị category
        tvInstructions = view.findViewById(R.id.tvInstructions)
        youtubeContainer = view.findViewById(R.id.youtubeContainer)
        _webView = view.findViewById(R.id.webYoutube)

        val foodId = requireArguments().getLong(ARG_FOOD_ID)

        // Collect dữ liệu từ Flow
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

        // Ảnh (imageRes là tên file trong res/drawable, ví dụ: "buncha")
        val resId = foodEntity.imageRes?.let { name ->
            resources.getIdentifier(name, "drawable", requireContext().packageName)
        } ?: 0

        if (resId != 0) {
            img.setImageResource(resId)
            img.contentDescription = foodEntity.title
        } else {
            img.setImageResource(R.drawable.ic_launcher_foreground)
            img.contentDescription = "No image"
        }


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

        // Youtube
        if (!foodEntity.youtubeId.isNullOrBlank()) {
            youtubeContainer.visibility = View.VISIBLE
            setupYoutubeWebView(foodEntity.youtubeId)
        } else {
            youtubeContainer.visibility = View.GONE
        }
    }

    private fun setupYoutubeWebView(videoId: String) {
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mediaPlaybackRequiresUserGesture = true

        webView.setBackgroundColor(Color.TRANSPARENT)

        val html = """
            <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                <style>html,body{margin:0;padding:0;background:transparent;}</style>
              </head>
              <body>
                <iframe
                  width="100%" height="100%"
                  src="https://www.youtube.com/embed/$videoId?autoplay=0&modestbranding=1&rel=0"
                  frameborder="0"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                  allowfullscreen>
                </iframe>
              </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }

    override fun onDestroyView() {
        _webView?.apply {
            loadUrl("about:blank")
            stopLoading()
            clearHistory()
            removeAllViews()
            destroy()
        }
        _webView = null
        super.onDestroyView()
    }
}
