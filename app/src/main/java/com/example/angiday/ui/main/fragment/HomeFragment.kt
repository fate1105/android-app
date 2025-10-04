package com.example.angiday.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.angiday.R
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.ui.main.adapter.SuggestionAdapter
import com.example.angiday.viewmodel.HomeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.example.angiday.db.AppDatabase
import com.example.angiday.repository.MetaRepository
import com.example.angiday.viewmodel.HomeViewModelFactory


class HomeFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var rvSuggestions: RecyclerView
    private lateinit var adapter: SuggestionAdapter
    private lateinit var btnFindRecipes: Button

    private lateinit var viewModel: HomeViewModel
    private var allSuggestions: List<String> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val dao = AppDatabase.get(requireContext()).metaDao()
        val repo = MetaRepository(dao)
        val factory = HomeViewModelFactory(repo)

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        etSearch = view.findViewById(R.id.etSearch)
        chipGroup = view.findViewById(R.id.chipGroupSelected)
        rvSuggestions = view.findViewById(R.id.rvSuggestions)
        btnFindRecipes = view.findViewById(R.id.btnFindRecipes)

        rvSuggestions.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = SuggestionAdapter(allSuggestions.toMutableList()) { addChip(it) }
        rvSuggestions.adapter = adapter

        setupSearchFilter()
        setupUnfocus(view)

        // Bắt đầu collect sau khi đã có viewModel
        lifecycleScope.launch {
            viewModel.ingredients.collectLatest { ingredients ->
                allSuggestions = ingredients.map { it.name }
                adapter.updateData(allSuggestions)
            }
        }

        btnFindRecipes.setOnClickListener {
            val selectedIngredients = mutableListOf<String>()
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                selectedIngredients.add(chip.text.toString())
            }

            val bundle = Bundle().apply {
                putStringArray("ingredients", selectedIngredients.toTypedArray())
            }

            val recipeFragment = SuggestFragment()
            recipeFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, recipeFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }


    private fun setupSearchFilter() {
        etSearch.addTextChangedListener { input ->
            val query = input?.toString()?.lowercase() ?: ""
            val filtered = if (query.isEmpty()) allSuggestions
            else allSuggestions.filter { it.lowercase().contains(query) }

            rvSuggestions.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
            adapter.updateData(filtered) // chỉ update, không tạo lại adapter
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
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as? android.view.inputmethod.InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

}

