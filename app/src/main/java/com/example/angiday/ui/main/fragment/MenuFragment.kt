package com.example.angiday.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var adapterMonNuoc: FoodAdapter
    private lateinit var adapterMonChien: FoodAdapter
    private lateinit var adapterMonCom: FoodAdapter
    private lateinit var adapterMonChay: FoodAdapter
    private lateinit var adapterMonXao: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rvNuoc = view.findViewById<RecyclerView>(R.id.rvMonNuoc)
        val rvChien = view.findViewById<RecyclerView>(R.id.rvMonChien)
        val rvCom = view.findViewById<RecyclerView>(R.id.rvMonCom)
        val rvChay = view.findViewById<RecyclerView>(R.id.rvMonChay)
        val rvXao = view.findViewById<RecyclerView>(R.id.rvMonXao)

        // Layout cho danh sách
        listOf(rvNuoc, rvChien, rvCom, rvChay, rvXao).forEach {
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        // Adapter có callback click
        adapterMonNuoc = FoodAdapter { foodId -> openFoodDetail(foodId) }
        adapterMonChien = FoodAdapter { foodId -> openFoodDetail(foodId) }
        adapterMonCom = FoodAdapter { foodId -> openFoodDetail(foodId) }
        adapterMonChay = FoodAdapter { foodId -> openFoodDetail(foodId) }
        adapterMonXao = FoodAdapter { foodId -> openFoodDetail(foodId) }

        rvNuoc.adapter = adapterMonNuoc
        rvChien.adapter = adapterMonChien
        rvCom.adapter = adapterMonCom
        rvChay.adapter = adapterMonChay
        rvXao.adapter = adapterMonXao

        val dao = AppDatabase.get(requireContext()).foodDao()
        val repo = FoodRepository(dao)
        val factory = MenuViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[MenuViewModel::class.java]

        // Quan sát dữ liệu DB
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.groupedFoods.collect { grouped ->
                adapterMonNuoc.submitList(grouped["Món nước"] ?: emptyList())
                adapterMonChien.submitList(grouped["Món chiên"] ?: emptyList())
                adapterMonCom.submitList(grouped["Món cơm"] ?: emptyList())
                adapterMonChay.submitList(grouped["Món chay"] ?: emptyList())
                adapterMonXao.submitList(grouped["Món xào"] ?: emptyList())
            }
        }
    }

    // Khi click vào món → mở chi tiết
    private fun openFoodDetail(foodId: Long) {
        Log.d("DEBUG_CLICK", "Clicked food id = $foodId")

        val fragment = FoodDetailFragment.newInstance(foodId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // id trong activity_main.xml
            .addToBackStack(null)
            .commit()
    }
}
