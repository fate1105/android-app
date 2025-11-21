package com.example.angiday.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.repository.FoodRepository
import com.example.angiday.ui.main.adapter.FoodAdapter
import com.example.angiday.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.angiday.viewmodel.CategoryViewModelFactory

class CategoryFragment : Fragment() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: FoodAdapter

    companion object {
        fun newInstance(categoryName: String): CategoryFragment {
            val fragment = CategoryFragment()
            val bundle = Bundle().apply {
                putString("categoryName", categoryName)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvTitle = view.findViewById<TextView>(R.id.tvCategoryTitle)
        val rvFoods = view.findViewById<RecyclerView>(R.id.rvCategoryFoods)

        rvFoods.layoutManager = LinearLayoutManager(requireContext())
        adapter = FoodAdapter { foodId -> openFoodDetail(foodId) }
        rvFoods.adapter = adapter

        val categoryName = arguments?.getString("categoryName") ?: ""

        tvTitle.text = categoryName

        val dao = AppDatabase.get(requireContext()).foodDao()
        val repo = FoodRepository(dao)
        val factory = CategoryViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        lifecycleScope.launch {
            viewModel.loadCategoryFoods(categoryName)
            viewModel.foods.collect { list ->
                adapter.submitList(list)
            }
        }
    }

    private fun openFoodDetail(foodId: Long) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FoodDetailFragment.newInstance(foodId))
            .addToBackStack(null)
            .commit()
    }
}
