package com.example.angiday.ui.wheel

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.angiday.databinding.ActivitySpinWheelBinding
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.relations.FoodWithRelations
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SpinWheelActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpinWheelBinding
    private val db by lazy { AppDatabase.get(this) }

    // ViewModel
    private val vm: SpinWheelViewModel by viewModels {
        SpinWheelViewModelFactory(db)
    }

    // Dữ liệu món ăn hiện tại (để mở chi tiết sau khi quay)
    private var currentFoods: List<FoodWithRelations> = emptyList()

    // Bảng màu xoay vòng
    private val wheelColors = listOf(
        Color.parseColor("#FF6F3C"),
        Color.parseColor("#FFCA28"),
        Color.parseColor("#8BC34A"),
        Color.parseColor("#FF9800"),
        Color.parseColor("#4CAF50"),
        Color.parseColor("#03A9F4"),
        Color.parseColor("#EC407A")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpinWheelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("type") ?: "random"

        // Đặt tiêu đề theo chế độ quay
        binding.tvTitle.text = if (type == "favorite")
            "Quay món yêu thích!"
        else
            "Quay món ngẫu nhiên!"

        // Load data theo type
        if (type == "favorite") {
            val userId = 1 // TODO: lấy từ SessionManager
            vm.loadFavoriteFoods(userId)
        } else {
            vm.loadRandomFoods(7)
        }

        observeFoods(type)

        // Ẩn card kết quả
        binding.resultCard.visibility = View.GONE

        // Sự kiện nhấn nút quay
        binding.btnSpin.setOnClickListener {
            binding.btnSpin.isEnabled = false

            binding.spinWheel.spin { selectedName ->
                binding.btnSpin.isEnabled = true
                showResult(selectedName)
            }
        }
    }

    private fun observeFoods(type: String) {
        lifecycleScope.launch {
            val flow =
                if (type == "favorite") vm.favoriteFoods
                else vm.randomFoods

            flow.collectLatest { foods ->
                if (foods.isNullOrEmpty()) return@collectLatest

                currentFoods = foods

                // Map thành các WheelItem (text + màu)
                val items = foods.mapIndexed { i, item ->
                    SpinWheelView.WheelItem(
                        text = item.food.title,
                        color = wheelColors[i % wheelColors.size]
                    )
                }

                // Đổ vào vòng quay
                binding.spinWheel.setItems(items)
            }
        }
    }

    private fun showResult(selectedName: String) {
        binding.tvResult.text = selectedName
        binding.resultCard.visibility = View.VISIBLE

        // animation scale + fade in
        binding.resultCard.apply {
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(250)
                .start()
        }

        // Click để mở chi tiết món
        binding.resultCard.setOnClickListener {
            val selectedFood = currentFoods.firstOrNull {
                it.food.title == selectedName
            }
            selectedFood?.let { openFoodDetail(it.food.id) }
        }
    }

    private fun openFoodDetail(id: Long) {
        val result = Intent().apply {
            putExtra("foodId", id)
        }
        setResult(RESULT_OK, result)
        finish()
    }
}
