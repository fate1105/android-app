package com.example.angiday.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.angiday.R
import com.example.angiday.databinding.FragmentShoppingListBinding
import com.example.angiday.ui.main.adapter.ShoppingAdapter
import com.example.angiday.utils.ShoppingPrefs

class ShoppingListFragment : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: ShoppingPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs = ShoppingPrefs(requireContext())

        val items = prefs.getItems()
        val adapter = ShoppingAdapter(items) { item ->
            prefs.removeItem(item)
        }

        binding.rvShopping.layoutManager = LinearLayoutManager(requireContext())
        binding.rvShopping.adapter = adapter

        // nút xóa tất cả
        binding.btnClearAll.setOnClickListener {
            prefs.clear()
            items.clear()
            adapter.notifyDataSetChanged()
            updateEmptyState(items)
        }

        // nút khám phá món ăn
        binding.btnGoExplore.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MenuFragment())
                .commit()
        }


        // cập nhật UI khi mở trang
        updateEmptyState(items)
    }

    private fun updateEmptyState(items: List<String>) {
        if (items.isEmpty()) {
            binding.emptyLayout.visibility = View.VISIBLE
            binding.rvShopping.visibility = View.GONE
            binding.btnClearAll.visibility = View.GONE
        } else {
            binding.emptyLayout.visibility = View.GONE
            binding.rvShopping.visibility = View.VISIBLE
            binding.btnClearAll.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
