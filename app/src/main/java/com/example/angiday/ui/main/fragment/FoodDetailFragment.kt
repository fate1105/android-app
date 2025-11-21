package com.example.angiday.ui.main.fragment

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserBehaviorEntity
import com.example.angiday.model.relations.FoodWithRelations
import com.example.angiday.repository.FoodRepository
import com.example.angiday.session.SessionManager
import com.example.angiday.utils.ImageUtils
import com.example.angiday.viewmodel.FoodDetailViewModel
import com.example.angiday.viewmodel.FoodDetailViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FoodDetailFragment : Fragment() {

    companion object {
        private const val ARG_FOOD_ID = "arg_food_id"
        fun newInstance(foodId: Long) = FoodDetailFragment().apply {
            arguments = Bundle().apply { putLong(ARG_FOOD_ID, foodId) }
        }
    }

    private lateinit var btnFavorite: ImageView
    private lateinit var btnCook: MaterialButton
    private lateinit var btnShare: ImageView

    private var isFavorite = false
    private var isCooked = false

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
    ): View = inflater.inflate(R.layout.fragment_food_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        img = view.findViewById(R.id.imgFoodDetail)
        tvTitle = view.findViewById(R.id.tvFoodTitle)
        tvDesc = view.findViewById(R.id.tvFoodDesc)
        chipGroupIngredients = view.findViewById(R.id.chipGroupIngredients)
        chipGroupTags = view.findViewById(R.id.chipGroupTags)
        tvCategory = view.findViewById(R.id.tvCategory)
        tvInstructions = view.findViewById(R.id.tvInstructions)
        youtubeContainer = view.findViewById(R.id.youtubeContainer)

        btnFavorite = view.findViewById(R.id.btnFavorite)
        btnCook = view.findViewById(R.id.btnCook)
        btnShare = view.findViewById(R.id.btnShare)

        val foodId = requireArguments().getLong(ARG_FOOD_ID)
        val dao = AppDatabase.get(requireContext()).userBehaviorDao()
        val session = SessionManager(requireContext())
        val userId = session.getUserId()

        // Hiển thị chi tiết món ăn
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFood(foodId).collectLatest { food ->
                food?.let { bindFood(it) }
            }
        }

        // Kiểm tra trạng thái yêu thích & nấu
        viewLifecycleOwner.lifecycleScope.launch {
            isFavorite = dao.exists(userId, foodId, "favorite") > 0
            isCooked = dao.exists(userId, foodId, "cooked") > 0
            updateFavoriteIcon()
            updateCookButtonState(isCooked)
        }


        btnFavorite.setOnClickListener {

            if (userId == -1L) {
                Toast.makeText(requireContext(), "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                isFavorite = !isFavorite
                if (isFavorite) {
                    dao.insert(UserBehaviorEntity(userId = userId, foodId = foodId, behaviorType = "favorite"))
                } else {
                    dao.deleteBehavior(userId, foodId, "favorite")
                }
                updateFavoriteIcon()
            }
        }



        // 🍳 Nút “Đã nấu”
        btnCook.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                isCooked = !isCooked
                if (isCooked) {
                    dao.insert(
                        UserBehaviorEntity(
                            userId = userId,
                            foodId = foodId,
                            behaviorType = "cooked",
                            timestamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        )
                    )

                    Toast.makeText(requireContext(), "Đã lưu: món đã nấu ✔", Toast.LENGTH_SHORT).show()
                } else {
                    dao.deleteBehavior(userId, foodId, "cooked")
                    Toast.makeText(requireContext(), "Đã hủy trạng thái nấu món 🍳", Toast.LENGTH_SHORT).show()
                }
                updateCookButtonState(isCooked)
            }
        }

        // 📤 Nút chia sẻ
        btnShare.setOnClickListener { shareFood(foodId, userId) }
    }

    // -------------------- UI helpers --------------------

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
            btnFavorite.setColorFilter(Color.RED)
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border)
            btnFavorite.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }
    }

    private fun updateCookButtonState(isCooked: Boolean) {
        if (isCooked) {
            btnCook.text = "Đã hoàn thành món ✔"
            btnCook.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            btnCook.setTextColor(Color.WHITE)
        } else {
            btnCook.text = "Đã nấu 🍳"
            btnCook.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBackground))
            btnCook.setTextColor(Color.BLACK)
        }
    }

    private fun bindFood(food: FoodWithRelations) {
        val f = food.food
        tvTitle.text = f.title
        tvDesc.text = f.desc ?: "Không có mô tả"
        tvCategory.text = food.category?.name ?: "Không có danh mục"
        tvInstructions.text = f.instructions ?: "Chưa có hướng dẫn nấu chi tiết."

        // Ảnh
        val imageName = f.imageRes
        val drawableId = ImageUtils.getDrawableId(requireContext(), imageName)
        val cachedFile = File(requireContext().cacheDir, "$imageName.png")
        val cachedBitmap = ImageUtils.loadCachedImage(cachedFile)

        if (cachedBitmap != null) {
            img.setImageBitmap(cachedBitmap)
        } else {
            img.setImageResource(drawableId)
            val bitmap = BitmapFactory.decodeResource(resources, drawableId)
            try {
                FileOutputStream(cachedFile).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Ingredients
        chipGroupIngredients.removeAllViews()
        food.ingredients.forEach {
            chipGroupIngredients.addView(Chip(requireContext()).apply {
                text = it.name
                isClickable = false
                isCheckable = false
            })
        }

        // Tags
        chipGroupTags.removeAllViews()
        food.tags.forEach {
            chipGroupTags.addView(Chip(requireContext()).apply {
                text = it.name
                isClickable = false
                isCheckable = false
            })
        }

        // YouTube
        if (!f.youtubeId.isNullOrBlank()) {
            youtubeContainer.visibility = View.VISIBLE
            youtubeContainer.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${f.youtubeId}")))
            }
        } else {
            youtubeContainer.visibility = View.GONE
        }
    }

    private fun shareFood(foodId: Long, userId: Long) {
        val title = tvTitle.text.toString()
        val desc = tvDesc.text.toString()

        val bitmap = img.drawable.toBitmap()
        val file = File(requireContext().cacheDir, "shared_food.png")
        FileOutputStream(file).use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }

        val uri = androidx.core.content.FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "$title\n$desc")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val myAppIntent = Intent(requireContext(), com.example.angiday.ui.share.ShareActivity::class.java).apply {
            putExtra(Intent.EXTRA_TEXT, "$title\n$desc")
            putExtra(Intent.EXTRA_STREAM, uri.toString())
            putExtra("food_id", foodId)
        }

        val chooser = Intent.createChooser(shareIntent, "Chia sẻ món ăn qua...")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(myAppIntent))
        startActivity(chooser)

        // Lưu hành vi chia sẻ
        viewLifecycleOwner.lifecycleScope.launch {
            AppDatabase.get(requireContext()).userBehaviorDao().insert(
                UserBehaviorEntity(userId = userId, foodId = foodId, behaviorType = "shared")
            )
        }
    }
}
