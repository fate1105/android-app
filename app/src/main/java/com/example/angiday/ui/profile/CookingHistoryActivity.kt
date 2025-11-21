package com.example.angiday.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.db.dao.UserBehaviorDao
import com.example.angiday.model.entity.FoodEntity
import com.example.angiday.utils.EmojiUtils
import com.example.angiday.viewmodel.CookingHistoryViewModel
import com.google.android.material.appbar.MaterialToolbar
import kotlin.math.max
import kotlinx.coroutines.launch

class CookingHistoryActivity : AppCompatActivity() {

    private val vm: CookingHistoryViewModel by viewModels()

    // UI streak
    private lateinit var flameContainer: LinearLayout
    private lateinit var tvStreak: TextView
    private lateinit var tvTotalDays: TextView
    private lateinit var tvBestStreak: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var imgBadge: ImageView
    private lateinit var bounceAnim: Animation

    // UI món đã nấu
    private lateinit var rvFoods: RecyclerView
    private lateinit var foodAdapter: CookedFoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cooking_history)

        // 🔙 Toolbar
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Animation
        bounceAnim = AnimationUtils.loadAnimation(this, R.anim.flame_bounce)

        // Bind View
        flameContainer = findViewById(R.id.flameContainer)
        tvStreak = findViewById(R.id.tvStreak)
        tvTotalDays = findViewById(R.id.tvTotalDays)
        tvBestStreak = findViewById(R.id.tvBestStreak)
        progressBar = findViewById(R.id.progressBar)
        imgBadge = findViewById(R.id.imgBadge)

        // ---------------------------
        // ⭐ RecyclerView món đã nấu
        // ---------------------------
        rvFoods = findViewById(R.id.rvCookedFoods)
        foodAdapter = CookedFoodAdapter()
        rvFoods.adapter = foodAdapter
        rvFoods.layoutManager = LinearLayoutManager(this)

        // Load món đã nấu
        loadCookedFoods()

        // Load dữ liệu streak
        vm.loadStreak(1)

        lifecycleScope.launchWhenResumed {
            vm.streak.collect { data ->
                renderStreakUI(data.current, data.total, data.best)
            }
        }
    }

    // ---------------------------
    // ⭐ LOAD DANH SÁCH MÓN ĐÃ NẤU
    // ---------------------------
    private fun loadCookedFoods(userId: Int = 1) {
        val db = AppDatabase.get(this)
        lifecycleScope.launch {
            val list = db.userBehaviorDao().getCookedFoods(userId)
            foodAdapter.submitList(list)
        }
    }

    // ---------------------------
    // ⭐ RENDER STREAK UI
    // ---------------------------
    private fun renderStreakUI(streak: Int, total: Int, best: Int) {

        flameContainer.removeAllViews()
        repeat(streak) {
            val flame = ImageView(this)
            val bitmap = EmojiUtils.textToBitmap("🔥", 70f, this)
            flame.setImageBitmap(bitmap)

            flame.layoutParams = LinearLayout.LayoutParams(70, 70).apply {
                setMargins(8, 0, 8, 0)
            }
            flame.startAnimation(bounceAnim)
            flameContainer.addView(flame)
        }

        // ⭐ Icon star
        val star = ImageView(this)
        star.setImageResource(R.drawable.ic_star)
        star.layoutParams = LinearLayout.LayoutParams(50, 50).apply {
            setMargins(8, 0, 8, 0)
        }
        flameContainer.addView(star)

        tvStreak.text = "$streak ngày liên tiếp"
        tvTotalDays.text = "Tổng: $total ngày"
        tvBestStreak.text = " • Kỷ lục: $best ngày"

        progressBar.max = max(30, streak)
        progressBar.progress = streak

        when {
            streak >= 14 -> {
                imgBadge.setImageResource(R.drawable.ic_badge_3)
                imgBadge.visibility = View.VISIBLE
            }
            streak >= 7 -> {
                imgBadge.setImageResource(R.drawable.ic_badge_2)
                imgBadge.visibility = View.VISIBLE
            }
            streak >= 3 -> {
                imgBadge.setImageResource(R.drawable.ic_badge_1)
                imgBadge.visibility = View.VISIBLE
            }
            else -> imgBadge.visibility = View.GONE
        }
    }
}

/* ------------------------------------------------------------------
   ⭐ COOKED FOOD ADAPTER — đặt trong cùng file cho gọn như bạn yêu cầu
------------------------------------------------------------------- */
class CookedFoodAdapter : RecyclerView.Adapter<CookedFoodAdapter.VH>() {

    private val items = mutableListOf<UserBehaviorDao.CookedFood>()

    fun submitList(list: List<UserBehaviorDao.CookedFood>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.foodImage)
        val name = view.findViewById<TextView>(R.id.foodName)
        val cookedDate = view.findViewById<TextView>(R.id.foodCookedDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cooked_food, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cooked = items[position]
        val item = cooked.food

        holder.name.text = item.title
        holder.cookedDate.text = formatDate(cooked.cookedTime)

        val context = holder.itemView.context
        val resId = context.resources.getIdentifier(item.imageRes, "drawable", context.packageName)
        if (resId != 0) holder.img.setImageResource(resId)
    }

    override fun getItemCount() = items.size

    private fun formatDate(raw: String?): String {
        if (raw.isNullOrEmpty()) return "Nấu ngày: ?"

        val input = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        val output = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())

        return try {
            val date = input.parse(raw)
            "Nấu ngày: ${output.format(date!!)}"
        } catch (e: Exception) {
            "Nấu ngày: $raw"
        }
    }
}
