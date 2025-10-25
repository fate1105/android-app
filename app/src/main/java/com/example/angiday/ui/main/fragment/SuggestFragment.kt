package com.example.angiday.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.repository.FoodRepository
import com.example.angiday.ui.main.adapter.FoodAdapter
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.angiday.viewmodel.*

class SuggestFragment : Fragment() {

    private lateinit var rvRecipes: RecyclerView
    private lateinit var tvSelectedIngredients: MaterialTextView
    private lateinit var adapter: FoodAdapter

    private val viewModel by viewModels<SuggestViewModel> {
        val dao = AppDatabase.get(requireContext()).foodDao()
        SuggestViewModelFactory(FoodRepository(dao))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_suggest, container, false)
        rvRecipes = view.findViewById(R.id.rvRecipes)
        tvSelectedIngredients = view.findViewById(R.id.tvSelectedIngredients)

        val ingredients = arguments?.getStringArray("ingredients")?.toList().orEmpty()
        tvSelectedIngredients.text =
            if (ingredients.isNotEmpty())
                "Nguyên liệu đã chọn: ${ingredients.joinToString(", ")}"
            else "Chưa chọn nguyên liệu nào"

        // RecyclerView + adapter
        adapter = FoodAdapter(onClick = { foodId ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FoodDetailFragment.newInstance(foodId))
                .addToBackStack(null)
                .commit()
        })
        rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        rvRecipes.adapter = adapter

        val flow = if (ingredients.isEmpty()) viewModel.foods
        else viewModel.foodsBy(ingredients)

        viewLifecycleOwner.lifecycleScope.launch {
            flow.collectLatest { adapter.submitList(it) }
        }

        return view
    }
}
