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
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.example.angiday.model.entity.UserBehaviorEntity
import com.example.angiday.session.SessionManager


class FoodDetailFragment : Fragment() {
    companion object {
        private const val ARG_FOOD_ID = "arg_food_id"
        fun newInstance(foodId: Long) = FoodDetailFragment().apply {
            arguments = Bundle().apply { putLong(ARG_FOOD_ID, foodId) }
        }
    }
    private lateinit var btnFavorite: ImageView
    private lateinit var btnCook: com.google.android.material.button.MaterialButton
    private lateinit var btnShare: ImageView
    private var isFavorite = false

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
        btnFavorite = view.findViewById(R.id.btnFavorite)
        btnCook = view.findViewById(R.id.btnCook)
        btnShare = view.findViewById(R.id.btnShare)

// Nút yêu thích ❤️
        // Nút yêu thích ❤️
        btnFavorite.setOnClickListener {
            val session = SessionManager(requireContext())
            val userId = session.getUserId()
            if (userId == -1L) {
                Toast.makeText(requireContext(), "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dao = AppDatabase.get(requireContext()).userBehaviorDao()

            isFavorite = !isFavorite
            viewLifecycleOwner.lifecycleScope.launch {
                if (isFavorite) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
                    btnFavorite.setColorFilter(Color.RED)
                    dao.insert(
                        UserBehaviorEntity(
                            userId = userId.toInt(),
                            foodId = foodId.toInt(),
                            behaviorType = "favorite"
                        )
                    )
                } else {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_border)
                    btnFavorite.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                    )
                    dao.delete(userId.toInt(), foodId.toInt())

                }
            }
        }

// Nút “Đã nấu 🍳”
        btnCook.setOnClickListener {
            // Đổi màu & chữ
            btnCook.text = "Đã hoàn thành món ✔"
            btnCook.setBackgroundColor(
                androidx.core.content.ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            )
            btnCook.setTextColor(android.graphics.Color.WHITE)

            // Ghi lại hành vi "cooked"
            viewLifecycleOwner.lifecycleScope.launch {
                val dao = AppDatabase.get(requireContext()).userBehaviorDao()
                val session = com.example.angiday.session.SessionManager(requireContext())
                val userId = session.getUserId()
                val foodId = requireArguments().getLong("arg_food_id")

                dao.insert(
                    com.example.angiday.model.entity.UserBehaviorEntity(
                        userId = userId.toInt(),
                        foodId = foodId.toInt(),
                        behaviorType = "cooked"
                    )
                )
            }
        }

// Nút “Chia sẻ 🔗”
        btnShare.setOnClickListener {
            val foodTitle = tvTitle.text.toString()
            val foodDesc = tvDesc.text.toString()

            // Lấy ảnh từ ImageView → convert thành file tạm
            img.isDrawingCacheEnabled = true
            val bitmap = android.graphics.Bitmap.createBitmap(img.drawingCache)
            img.isDrawingCacheEnabled = false

            val uri = androidx.core.content.FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                java.io.File(requireContext().cacheDir, "shared_food.png").apply {
                    java.io.FileOutputStream(this).use { out ->
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                    }
                }
            )

            // Intent chia sẻ đến app khác
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "$foodTitle\n$foodDesc")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Intent mở ShareActivity của chính app bạn
            val myAppIntent = Intent(requireContext(), com.example.angiday.ui.share.ShareActivity::class.java).apply {
                putExtra(Intent.EXTRA_TEXT, "$foodTitle\n$foodDesc")
                putExtra(Intent.EXTRA_STREAM, uri.toString())
                putExtra("food_id", foodId)
            }


            // Mở chooser → người dùng có thể chọn app ngoài hoặc chính app bạn
            val chooser = Intent.createChooser(shareIntent, "Chia sẻ món ăn qua...")
            val initialIntents = arrayOf(myAppIntent)
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents)
            startActivity(chooser)

            // Lưu vào DB hành vi "shared"
            viewLifecycleOwner.lifecycleScope.launch {
                val dao = AppDatabase.get(requireContext()).userBehaviorDao()
                val session = com.example.angiday.session.SessionManager(requireContext())
                val userId = session.getUserId()
                val foodId = requireArguments().getLong("arg_food_id")

                dao.insert(
                    com.example.angiday.model.entity.UserBehaviorEntity(
                        userId = userId.toInt(),
                        foodId = foodId.toInt(),
                        behaviorType = "shared"
                    )
                )
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
