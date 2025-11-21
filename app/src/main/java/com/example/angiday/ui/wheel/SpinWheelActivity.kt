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

    // VIEWMODEL
    private val vm: SpinWheelViewModel by viewModels {
        SpinWheelViewModelFactory(db)
    }

    // Danh sách món hiện đang được load (random hoặc favorite)
    private var currentFoods: List<FoodWithRelations> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpinWheelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("type") ?: "random"

        binding.tvTitle.text = if (type == "favorite")
            "Quay món yêu thích!"
        else
            "Quay món ngẫu nhiên!"

        // Load danh sách món theo kiểu quay
        if (type == "favorite") {
            val userId = 1   // TODO: lấy từ SessionManager
            vm.loadFavoriteFoods(userId)
        } else {
            vm.loadRandomFoods(7)
        }

        observeFoods(type)

        binding.resultCard.visibility = View.GONE

        binding.btnSpin.setOnClickListener {
            binding.btnSpin.isEnabled = false

            binding.spinWheel.spin { selectedName ->
                binding.btnSpin.isEnabled = true
                showResult(selectedName)
            }
        }
    }


    // Quan sát data từ ViewModel
    private fun observeFoods(type: String) {
        lifecycleScope.launch {
            val flow = if (type == "favorite") vm.favoriteFoods else vm.randomFoods

            flow.collectLatest { foods ->
                if (foods.isNullOrEmpty()) return@collectLatest

                currentFoods = foods

                val colors = listOf(
                    Color.parseColor("#FF6F3C"),
                    Color.parseColor("#FFCA28"),
                    Color.parseColor("#8BC34A"),
                    Color.parseColor("#FF9800"),
                    Color.parseColor("#4CAF50"),
                    Color.parseColor("#03A9F4"),
                    Color.parseColor("#EC407A")
                )

                val items = foods.mapIndexed { i, food ->
                    SpinWheelView.WheelItem(
                        text = food.food.title,
                        color = colors[i % colors.size]
                    )
                }

                binding.spinWheel.setItems(items)
            }
        }
    }


    private fun showResult(selectedName: String) {
        binding.tvResult.text = selectedName
        binding.resultCard.visibility = View.VISIBLE

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

        // Click mở chi tiết theo foodId
        binding.resultCard.setOnClickListener {
            val selected = currentFoods.firstOrNull { it.food.title == selectedName }
            selected?.let { openFoodDetail(it.food.id) }
        }
    }


    private fun openFoodDetail(id: Long) {
        val intent = Intent()
        intent.putExtra("foodId", id)
        setResult(RESULT_OK, intent)
        finish()
    }
}
