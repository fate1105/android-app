package com.example.angiday.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.example.angiday.ui.main.adapter.FoodAdapter
import com.example.angiday.ui.main.model.Food

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Ánh xạ 3 RecyclerView
        val rvMorning = view.findViewById<RecyclerView>(R.id.rvFood)   // sáng
        val rvNoon = view.findViewById<RecyclerView>(R.id.rvFood2)     // trưa
        val rvEvening = view.findViewById<RecyclerView>(R.id.rvFood3)  // tối

        // Món ăn sáng
        val foodsMorning = listOf(
            Food("Phở bò", "Nước dùng đậm, bò tái.", R.drawable.logo),
            Food("Bánh mì", "Pate, dưa leo.", R.drawable.logo),
            Food("Xôi gà", "Xôi nếp thơm, gà xé.", R.drawable.logo)
        )

        // Món ăn trưa
        val foodsNoon = listOf(
            Food("Cơm tấm", "Sườn bì chả.", R.drawable.logo),
            Food("Canh chua cá", "Chua ngọt thanh mát.", R.drawable.logo),
            Food("Thịt kho tàu", "Ngon với cơm trắng.", R.drawable.logo)
        )

        // Món ăn tối
        val foodsEvening = listOf(
            Food("Bún bò Huế", "Cay nhẹ, thơm sả.", R.drawable.logo),
            Food("Lẩu thái", "Đậm đà, hải sản tươi.", R.drawable.logo),
            Food("Gỏi cuốn", "Thanh mát, ít dầu mỡ.", R.drawable.logo)
        )

        // Setup RecyclerView sáng
        rvMorning.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvMorning.setHasFixedSize(true)
        rvMorning.adapter = FoodAdapter(foodsMorning) { /* handle click */ }

        // Setup RecyclerView trưa
        rvNoon.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvNoon.setHasFixedSize(true)
        rvNoon.adapter = FoodAdapter(foodsNoon) { /* handle click */ }

        // Setup RecyclerView tối
        rvEvening.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvEvening.setHasFixedSize(true)
        rvEvening.adapter = FoodAdapter(foodsEvening) { /* handle click */ }
    }
}
