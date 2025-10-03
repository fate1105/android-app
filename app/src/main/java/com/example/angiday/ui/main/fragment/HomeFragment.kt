package com.example.angiday.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.angiday.R
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.ui.main.adapter.SuggestionAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class HomeFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var rvSuggestions: RecyclerView
    private lateinit var adapter: SuggestionAdapter
    private lateinit var btnFindRecipes: MaterialButton

    private val allSuggestions = listOf(
        "Cơm chiên", "Phở bò", "Mì xào",
        "Cà chua", "Cải xanh", "Cá hồi", "Cánh gà"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        etSearch = view.findViewById(R.id.etSearch)
        chipGroup = view.findViewById(R.id.chipGroupSelected)
        rvSuggestions = view.findViewById(R.id.rvSuggestions)
        btnFindRecipes = view.findViewById(R.id.btnFindRecipes)

        // RecyclerView setup 3 cột
        rvSuggestions.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = SuggestionAdapter(allSuggestions.toMutableList()) { addChip(it) }
        rvSuggestions.adapter = adapter

        setupSearchFilter()
        setupUnfocus(view)

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
                .addToBackStack(null) // để có thể bấm back quay lại Home
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

