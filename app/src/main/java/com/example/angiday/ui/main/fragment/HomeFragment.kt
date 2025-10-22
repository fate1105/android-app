package com.example.angiday.ui.main.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import android.widget.LinearLayout
import android.widget.TextView

import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.repository.MetaRepository
import com.example.angiday.service.FoodSuggestionService
import com.example.angiday.ui.main.adapter.SuggestionAdapter
import com.example.angiday.viewmodel.HomeViewModel
import com.example.angiday.viewmodel.HomeViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModelProvider
import com.example.angiday.db.AppDatabase
import com.example.angiday.repository.FoodRepository
import com.example.angiday.repository.MetaRepository
import com.example.angiday.viewmodel.HomeViewModelFactory


import android.widget.Toast
import android.widget.TextView
// Trong class HomeFragment
private lateinit var tvRandomFood: TextView
private lateinit var receiver: BroadcastReceiver
class HomeFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var rvSuggestions: RecyclerView
    private lateinit var adapter: SuggestionAdapter
    private lateinit var btnFindRecipes: Button

    private lateinit var tvBreakfast: TextView
    private lateinit var tvLunch: TextView
    private lateinit var tvDinner: TextView
    private lateinit var categoryContainer: LinearLayout

    private lateinit var viewModel: HomeViewModel
    private var allSuggestions: List<String> = emptyList()

    // 🔹 BroadcastReceiver nhận món mới từ Service
    private lateinit var receiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // --- Khởi tạo ViewModel ---
        val dao = AppDatabase.get(requireContext()).metaDao()
        val repo = MetaRepository(dao)
        val factory = HomeViewModelFactory(repo)

        // --- Lấy instance database
        val db = AppDatabase.get(requireContext())

        val metaRepo = MetaRepository(db.metaDao())
        val foodRepo = FoodRepository(db.foodDao())

        val factory = HomeViewModelFactory(metaRepo, foodRepo)

        // --- Tạo ViewModel

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // --- Ánh xạ View ---
        etSearch = view.findViewById(R.id.etSearch)
        chipGroup = view.findViewById(R.id.chipGroupSelected)
        rvSuggestions = view.findViewById(R.id.rvSuggestions)
        btnFindRecipes = view.findViewById(R.id.btnFindRecipes)
        tvBreakfast = view.findViewById(R.id.tvBreakfast)
        tvLunch = view.findViewById(R.id.tvLunch)
        tvDinner = view.findViewById(R.id.tvDinner)

        // --- Thiết lập RecyclerView ---
        rvSuggestions.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = SuggestionAdapter(allSuggestions.toMutableList()) { addChip(it) }
        rvSuggestions.adapter = adapter

        setupSearchFilter()
        setupUnfocus(view)

        // --- Lấy dữ liệu từ ViewModel ---
        lifecycleScope.launch {
            viewModel.ingredients.collectLatest { ingredients ->
                allSuggestions = ingredients.map { it.name }
                adapter.updateData(allSuggestions)
            }
        }

        // 👉 Khởi động Service khi mở HomeFragment
        tvRandomFood = view.findViewById(R.id.tvRandomFood)

// 👉 Khởi động Service
        requireContext().startService(Intent(requireContext(), FoodSuggestionService::class.java))

// 👉 Nhận dữ liệu từ Broadcast
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val newFood = intent?.getStringExtra("foodName") ?: return

                // Cập nhật TextView hiển thị món ăn ngẫu nhiên
                tvRandomFood.text = "🥢 Món gợi ý: $newFood"

                // (Tùy chọn) hiện Toast để dễ quay video demo
                Toast.makeText(requireContext(), "Món mới: $newFood", Toast.LENGTH_SHORT).show()
            }
        }

// Đăng ký BroadcastReceiver – dùng requireActivity() để chắc chắn nhận được
        ContextCompat.registerReceiver(
            requireActivity(),
            receiver,
            IntentFilter("NEW_FOOD_SUGGESTED"),
            ContextCompat.RECEIVER_EXPORTED // ✅ cho phép nhận khi app đang foreground
        )



        // 🔹 Nút "Tìm công thức"
        btnFindRecipes.setOnClickListener {
            val selectedIngredients = mutableListOf<String>()
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                selectedIngredients.add(chip.text.toString())
            }

            val bundle = Bundle().apply {
                putStringArray("ingredients", selectedIngredients.toTypedArray())
            }

            val suggestFragment = SuggestFragment()
            suggestFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, suggestFragment)
                .addToBackStack(null)
                .commit()
        }

        viewModel.loadRandomMeals()

        lifecycleScope.launch {
            viewModel.randomMeals.collectLatest { meals ->
                if (meals.size >= 3) {
                    tvBreakfast.text = "Bữa sáng:\n${meals[0].food.title}"
                    tvLunch.text = "Bữa trưa:\n${meals[1].food.title}"
                    tvDinner.text = "Bữa tối:\n${meals[2].food.title}"

                }
            }
        }
        categoryContainer = view.findViewById(R.id.categoryContainer)

        lifecycleScope.launch {
            viewModel.categories.collectLatest { categories ->
                categoryContainer.removeAllViews()
                for (category in categories) {
                    val textView = TextView(requireContext()).apply {
                        text = category.name
                        setBackgroundResource(R.drawable.bg_chip_simple)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                        setPadding(24, 12, 24, 12)

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 16, 0)
                        }
                        layoutParams = params
                    }

                    categoryContainer.addView(textView)
                }
            }
        }
main

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 🧹 Huỷ đăng ký Receiver và dừng Service khi thoát Fragment
        requireContext().unregisterReceiver(receiver)
        requireContext().stopService(Intent(requireContext(), FoodSuggestionService::class.java))
    }

    // ----------------- Các hàm phụ -----------------

    private fun setupSearchFilter() {
        etSearch.addTextChangedListener { input ->
            val query = input?.toString()?.lowercase() ?: ""
            val filtered = if (query.isEmpty()) allSuggestions
            else allSuggestions.filter { it.lowercase().contains(query) }

            rvSuggestions.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
            adapter.updateData(filtered)
        }
    }

    private fun addChip(item: String) {
        val chip = Chip(requireContext()).apply {
            text = item
            isCloseIconVisible = true
            setOnCloseIconClickListener { chipGroup.removeView(this) }
        }
        chipGroup.addView(chip)
    }

    private fun setupUnfocus(rootView: View) {
        rootView.setOnTouchListener { v, _ ->
            etSearch.clearFocus()
            hideKeyboard(etSearch)
            v.performClick()
            false
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                as? android.view.inputmethod.InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }


}
