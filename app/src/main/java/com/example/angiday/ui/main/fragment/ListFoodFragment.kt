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
//import com.example.angiday.model.Food

class ListFoodFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_listfood, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rvFood)

//        val foods = listOf(
//            Food("Phở bò", "Nước dùng đậm, bò tái.", R.drawable.logo),
//            Food("Bún bò Huế", "Cay nhẹ, thơm sả.", R.drawable.logo),
//            Food("Cơm tấm", "Sườn bì chả.", R.drawable.logo),
//            Food("Bánh mì", "Pate, dưa leo.", R.drawable.logo)
//        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.setHasFixedSize(true)
//        rv.adapter = FoodAdapter(foods) { /* handle click nếu cần */ }

    }

}
