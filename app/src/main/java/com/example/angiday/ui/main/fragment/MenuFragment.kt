package com.example.angiday.ui.main.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.repository.FoodRepository
import com.example.angiday.ui.main.adapter.FoodAdapter
import com.example.angiday.viewmodel.MenuViewModel
import com.example.angiday.viewmodel.factory.MenuViewModelFactory
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private lateinit var viewModel: MenuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val container = view.findViewById<LinearLayout>(R.id.containerMenu)

        val dao = AppDatabase.get(requireContext()).foodDao()
        val repo = FoodRepository(dao)
        val factory = MenuViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[MenuViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.groupedFoods.collect { grouped ->

                container.removeAllViews()

                grouped.forEach { (categoryName, foods) ->

                    // -----------------------------
                    //   HEADER ROW (TITLE + XEM THÊM)
                    // -----------------------------
                    val headerRow = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    // TITLE CATEGORY
                    val tvCategory = TextView(requireContext()).apply {
                        text = categoryName
                        textSize = 20f
                        typeface = Typeface.DEFAULT_BOLD
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSurface))
                        setPadding(0, 20, 0, 10)
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    }

                    // "Xem thêm >"
                    val tvMore = TextView(requireContext()).apply {
                        text = "Xem thêm >"
                        textSize = 14f
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSurface))
                        setPadding(0, 20, 0, 10)
                        setOnClickListener {
                            openCategory(categoryName)
                        }
                    }

                    headerRow.addView(tvCategory)
                    headerRow.addView(tvMore)

                    container.addView(headerRow)

                    // --------------------------------
                    //   RECYCLERVIEW NGANG
                    // --------------------------------
                    val rv = RecyclerView(requireContext()).apply {

                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            RecyclerView.HORIZONTAL,
                            false
                        )

                        isNestedScrollingEnabled = false

                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            bottomMargin = 12
                        }
                    }

                    val adapter = FoodAdapter { foodId -> openFoodDetail(foodId) }
                    rv.adapter = adapter
                    adapter.submitList(foods)

                    container.addView(rv)
                }
            }
        }
    }

    // mở FoodDetail
    private fun openFoodDetail(foodId: Long) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FoodDetailFragment.newInstance(foodId))
            .addToBackStack(null)
            .commit()
    }

    // mở CategoryFragment
    private fun openCategory(categoryName: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CategoryFragment.newInstance(categoryName))
            .addToBackStack(null)
            .commit()
    }
}
