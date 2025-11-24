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
import com.example.angiday.session.SessionManager
import com.example.angiday.ui.main.MainActivity
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

    private lateinit var etSearch: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var rvSuggestions: RecyclerView
    private lateinit var btnFindRecipes: Button
    private lateinit var imgFeatured: ImageView
    private lateinit var tvBreakfast: TextView
    private lateinit var tvLunch: TextView
    private lateinit var tvDinner: TextView
    private lateinit var btnMenu: Button
    private lateinit var categoryContainer: LinearLayout

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: SuggestionAdapter
    private var allSuggestions = emptyList<String>()

    private var lastCategoryList = emptyList<String>()   // ⭐ cache category

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setupViewModel()
        bindViews(view)
        setupRecyclerView()
        setupListeners(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔥 CHỈ LOAD 1 LẦN
        viewModel.loadHomeData(requireContext())

        observeData()
    }

    private fun setupViewModel() {
        val db = AppDatabase.get(requireContext())
        val session = SessionManager(requireContext())
        val metaRepo = MetaRepository(db.metaDao(), db.userProfileDao(), session)
        val foodRepo = FoodRepository(db.foodDao())
        viewModel = ViewModelProvider(this, HomeViewModelFactory(metaRepo, foodRepo))[HomeViewModel::class.java]
    }

    private fun bindViews(view: View) {
        etSearch = view.findViewById(R.id.etSearch)
        chipGroup = view.findViewById(R.id.chipGroupSelected)
        rvSuggestions = view.findViewById(R.id.rvSuggestions)
        btnFindRecipes = view.findViewById(R.id.btnFindRecipes)
        imgFeatured = view.findViewById(R.id.imgFeatured)
        tvBreakfast = view.findViewById(R.id.tvBreakfast)
        tvLunch = view.findViewById(R.id.tvLunch)
        tvDinner = view.findViewById(R.id.tvDinner)
        categoryContainer = view.findViewById(R.id.categoryContainer)
        btnMenu = view.findViewById(R.id.btnMenu)
    }

    private fun setupRecyclerView() {
        rvSuggestions.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = SuggestionAdapter(allSuggestions.toMutableList()) { addChip(it) }
        rvSuggestions.adapter = adapter
    }

    private fun setupListeners(rootView: View) {
        setupSearchFilter()

        etSearch.setOnFocusChangeListener { _, hasFocus ->
            rvSuggestions.visibility =
                if (hasFocus && etSearch.text.isEmpty()) View.VISIBLE else View.GONE
        }

        rootView.setOnTouchListener { v, _ ->
            etSearch.clearFocus()
            hideKeyboard(etSearch)
            v.performClick()
            false
        }

        btnFindRecipes.setOnClickListener { navigateToSuggestFragment() }
        btnMenu.setOnClickListener { openMenuFragment() }
    }

    private fun setupSearchFilter() {
        etSearch.addTextChangedListener { input ->
            val query = input?.toString()?.lowercase() ?: ""
            val filtered =
                if (query.isEmpty()) allSuggestions
                else allSuggestions.filter { it.lowercase().contains(query) }

            rvSuggestions.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
            adapter.updateData(filtered)
        }
    }

    private fun observeData() {

        // ========== INGREDIENTS ==========
        lifecycleScope.launch {
            viewModel.ingredients.collect { ingredients ->
                val list = ingredients.map { it.name }

                if (list != allSuggestions) {
                    allSuggestions = list
                    adapter.updateData(list)
                }
            }
        }

        // ========== FEATURED FOOD ==========
        lifecycleScope.launch {
            viewModel.featuredFood.collect { food ->
                food ?: return@collect

                val resId = resources.getIdentifier(
                    food.imageRes, "drawable", requireContext().packageName
                )

                imgFeatured.alpha = 0f
                imgFeatured.setImageResource(if (resId != 0) resId else R.drawable.ic_menu)
                imgFeatured.animate().alpha(1f).setDuration(400).start()

                imgFeatured.setOnClickListener {
                    val id = viewModel.featuredFoodId.value ?: return@setOnClickListener
                    openFoodDetail(id)
                }
            }
        }

        // ========== RANDOM MEALS ==========
        lifecycleScope.launch {
            viewModel.randomMeals.collect { meals ->
                if (meals.isEmpty()) return@collect

                val breakfast = meals.getOrNull(0)
                val lunch = meals.getOrNull(1)
                val dinner = meals.getOrNull(2)

                tvBreakfast.text = "Bữa sáng:\n${breakfast?.food?.title ?: "Không có món"}"
                tvLunch.text     = "Bữa trưa:\n${lunch?.food?.title ?: "Không có món"}"
                tvDinner.text    = "Bữa tối:\n${dinner?.food?.title ?: "Không có món"}"

                tvBreakfast.setOnClickListener { breakfast?.let { openFoodDetail(it.food.id) } }
                tvLunch.setOnClickListener { lunch?.let { openFoodDetail(it.food.id) } }
                tvDinner.setOnClickListener { dinner?.let { openFoodDetail(it.food.id) } }
            }
        }

        // ========== CATEGORIES ==========
        lifecycleScope.launch {
            viewModel.categories.collect { list ->
                val names = list.map { it.name }

                // 🔥 Không render lại nếu không đổi dữ liệu
                if (names == lastCategoryList) return@collect

                lastCategoryList = names
                categoryContainer.removeAllViews()

                names.forEach { name ->
                    val tv = TextView(requireContext()).apply {
                        text = name
                        setBackgroundResource(R.drawable.bg_chip_simple)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                        setPadding(24, 12, 24, 12)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply { setMargins(0, 0, 16, 0) }

                        setOnClickListener { openCategoryFragment(name) }
                    }
                    categoryContainer.addView(tv)
                }
            }
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

    private fun navigateToSuggestFragment() {
        val selectedIngredients = (0 until chipGroup.childCount).map {
            (chipGroup.getChildAt(it) as Chip).text.toString()
        }

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                SuggestFragment().apply { arguments = Bundle().apply {
                    putStringArray("ingredients", selectedIngredients.toTypedArray())
                }})
            .addToBackStack(null)
            .commit()
    }

    private fun openMenuFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MenuFragment())
            .addToBackStack(null)
            .commit()
        (activity as? MainActivity)?.setBottomNavSelected(R.id.nav_menu)
    }

    private fun openCategoryFragment(categoryName: String) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                CategoryFragment.newInstance(categoryName)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun openFoodDetail(foodId: Long) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                FoodDetailFragment.newInstance(foodId)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
