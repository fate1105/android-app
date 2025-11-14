package com.example.angiday.ui.main.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.repository.FoodRepository
import com.example.angiday.repository.MetaRepository
import com.example.angiday.ui.main.adapter.SuggestionAdapter
import com.example.angiday.viewmodel.HomeViewModel
import com.example.angiday.viewmodel.HomeViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * HomeFragment
 * Màn hình chính của Angiday:
 * - Gợi ý nguyên liệu & tìm công thức
 * - Gợi ý bữa ăn ngẫu nhiên (sáng / trưa / tối)
 * - Danh mục món ăn phổ biến
 */
class HomeFragment : Fragment() {

    // UI components
    private lateinit var etSearch: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var rvSuggestions: RecyclerView
    private lateinit var btnFindRecipes: Button

    private lateinit var tvBreakfast: TextView
    private lateinit var tvLunch: TextView
    private lateinit var tvDinner: TextView
    private lateinit var categoryContainer: LinearLayout

    // ViewModel
    private lateinit var viewModel: HomeViewModel

    // Adapter
    private lateinit var adapter: SuggestionAdapter
    private var allSuggestions: List<String> = emptyList()

    // Lifecycle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setupViewModel()
        bindViews(view)
        setupRecyclerView()
        setupListeners(view)
        observeData()

        return view
    }

    // Setup ViewModel

    private fun setupViewModel() {
        val db = AppDatabase.get(requireContext())
        val metaRepo = MetaRepository(db.metaDao())
        val foodRepo = FoodRepository(db.foodDao())
        val factory = HomeViewModelFactory(metaRepo, foodRepo)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    // Ánh xạ View và UI khởi tạo

    private fun bindViews(view: View) {
        etSearch = view.findViewById(R.id.etSearch)
        chipGroup = view.findViewById(R.id.chipGroupSelected)
        rvSuggestions = view.findViewById(R.id.rvSuggestions)
        btnFindRecipes = view.findViewById(R.id.btnFindRecipes)

        tvBreakfast = view.findViewById(R.id.tvBreakfast)
        tvLunch = view.findViewById(R.id.tvLunch)
        tvDinner = view.findViewById(R.id.tvDinner)
        categoryContainer = view.findViewById(R.id.categoryContainer)
    }

    private fun setupRecyclerView() {
        rvSuggestions.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = SuggestionAdapter(allSuggestions.toMutableList()) { addChip(it) }
        rvSuggestions.adapter = adapter
    }

    // Thiết lập sự kiện & tương tác

    private fun setupListeners(rootView: View) {
        // Khi gõ tìm kiếm
        setupSearchFilter()

        // Khi focus vào ô tìm kiếm
        etSearch.setOnFocusChangeListener { _, hasFocus ->
            rvSuggestions.visibility =
                if (hasFocus && etSearch.text.isEmpty()) View.VISIBLE else View.GONE
        }

        // Khi nhấn ra ngoài → ẩn bàn phím + ẩn danh sách gợi ý
        setupUnfocus(rootView)

        // Khi nhấn nút “Tìm công thức”
        btnFindRecipes.setOnClickListener { navigateToSuggestFragment() }
    }

    // Tìm kiếm & chip nguyên liệu

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

    // Điều hướng & xử lý nút "Tìm công thức"

    private fun navigateToSuggestFragment() {
        val selectedIngredients = mutableListOf<String>().apply {
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                add(chip.text.toString())
            }
        }

        val bundle = Bundle().apply {
            putStringArray("ingredients", selectedIngredients.toTypedArray())
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SuggestFragment().apply { arguments = bundle })
            .addToBackStack(null)
            .commit()
    }

    // Quan sát dữ liệu từ ViewModel (Flow)

    private fun observeData() {
        // Gợi ý nguyên liệu
        lifecycleScope.launch {
            viewModel.ingredients.collectLatest { ingredients ->
                allSuggestions = ingredients.map { it.name }
                adapter.updateData(allSuggestions)
            }
        }

        // Gợi ý ngẫu nhiên cho 3 bữa
        viewModel.loadRandomMeals(requireContext())
        lifecycleScope.launch {
            viewModel.randomMeals.collectLatest { meals ->
                if (meals.size >= 3) {
                    tvBreakfast.text = "Bữa sáng:\n${meals[0].food.title}"
                    tvLunch.text = "Bữa trưa:\n${meals[1].food.title}"
                    tvDinner.text = "Bữa tối:\n${meals[2].food.title}"
                }
            }
        }

        // Danh mục món ăn
        lifecycleScope.launch {
            viewModel.categories.collectLatest { categories ->
                categoryContainer.removeAllViews()
                for (category in categories) {
                    val textView = TextView(requireContext()).apply {
                        text = category.name
                        setBackgroundResource(R.drawable.bg_chip_simple)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                        setPadding(24, 12, 24, 12)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { setMargins(0, 0, 16, 0) }
                    }
                    categoryContainer.addView(textView)
                }
            }
        }
    }

    // Xử lý UI phụ (ẩn bàn phím, mất focus)

    private fun setupUnfocus(rootView: View) {
        rootView.setOnTouchListener { v, _ ->
            etSearch.clearFocus()
            hideKeyboard(etSearch)
            v.performClick()
            false
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
