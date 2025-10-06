package com.example.angiday.ui.main.fragment

import android.os.Bundle
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

    // Thêm đủ 5 adapter
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
        // 1️⃣ Ánh xạ RecyclerView
        val rvNuoc = view.findViewById<RecyclerView>(R.id.rvMonNuoc)
        val rvChien = view.findViewById<RecyclerView>(R.id.rvMonChien)
        val rvCom = view.findViewById<RecyclerView>(R.id.rvMonCom)
        val rvChay = view.findViewById<RecyclerView>(R.id.rvMonChay)
        val rvXao = view.findViewById<RecyclerView>(R.id.rvMonXao)

        // 2️⃣ Gán LayoutManager cho từng RecyclerView
        listOf(rvNuoc, rvChien, rvCom, rvChay, rvXao).forEach {
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        // 3️⃣ Khởi tạo Adapter
        adapterMonNuoc = FoodAdapter()
        adapterMonChien = FoodAdapter()
        adapterMonCom = FoodAdapter()
        adapterMonChay = FoodAdapter()
        adapterMonXao = FoodAdapter()

        // 4️⃣ Gán Adapter cho RecyclerView
        rvNuoc.adapter = adapterMonNuoc
        rvChien.adapter = adapterMonChien
        rvCom.adapter = adapterMonCom
        rvChay.adapter = adapterMonChay
        rvXao.adapter = adapterMonXao

        // 5️⃣ Khởi tạo Repository + ViewModel đúng chuẩn
        val dao = AppDatabase.get(requireContext()).foodDao()
        val repo = FoodRepository(dao)
        val factory = MenuViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[MenuViewModel::class.java]

        // 6️⃣ Quan sát dữ liệu từ DB
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.groupedFoods.collect { grouped ->
                val monNuoc = grouped["Món nước"] ?: emptyList()
                val monChien = grouped["Món chiên"] ?: emptyList()
                val monCom = grouped["Món cơm"] ?: emptyList()
                val monChay = grouped["Món chay"] ?: emptyList()
                val monXao = grouped["Món xào"] ?: emptyList()

                adapterMonNuoc.submitList(monNuoc)
                adapterMonChien.submitList(monChien)
                adapterMonCom.submitList(monCom)
                adapterMonChay.submitList(monChay)
                adapterMonXao.submitList(monXao)

                println("DEBUG ➜ nước=${monNuoc.size}, chiên=${monChien.size}, cơm=${monCom.size}, chay=${monChay.size}, xào=${monXao.size}")
            }
        }
    }
}
