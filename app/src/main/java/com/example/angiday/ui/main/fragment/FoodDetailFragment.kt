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
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
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
    private lateinit var chipGroup: ChipGroup
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
        chipGroup = view.findViewById(R.id.chipGroupIngredients)
        tvInstructions = view.findViewById(R.id.tvInstructions)
        youtubeContainer = view.findViewById(R.id.youtubeContainer)
        _webView = view.findViewById(R.id.webYoutube)

        val foodId = requireArguments().getLong(ARG_FOOD_ID)

        // Quan sát dữ liệu từ ViewModel (Flow -> collect)
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.getFood(foodId).collectLatest { data ->
//                data?.let { bindFood(it.food.title, it.food.desc, it.food.imageRes, it.food.instructions, it.food.youtubeId, it.ingredients.map { ing -> ing.name }) }
//            }
//        }
    }

    private fun bindFood(
        title: String,
        desc: String?,           // cho phép null
        imageRes: Int?,
        instructions: String?,   // cho phép null
        youtubeId: String?,
        ingredients: List<String>
    ) {
        tvTitle.text = title
        tvDesc.text = desc ?: "Không có mô tả"

        if (imageRes != null) img.setImageResource(imageRes)
        else img.setImageResource(R.drawable.ic_launcher_foreground)

        chipGroup.removeAllViews()
        ingredients.forEach { ing ->
            val chip = Chip(requireContext()).apply {
                text = ing
                isCheckable = false
                isClickable = false
            }
            chipGroup.addView(chip)
        }

        tvInstructions.text = instructions ?: "Chưa có hướng dẫn nấu chi tiết."

        if (!youtubeId.isNullOrBlank()) {
            youtubeContainer.visibility = View.VISIBLE
            setupYoutubeWebView(youtubeId)
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
