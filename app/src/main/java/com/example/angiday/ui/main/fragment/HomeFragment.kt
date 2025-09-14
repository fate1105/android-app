package com.example.angiday.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.angiday.R
import com.example.angiday.ui.main.listener.ClickListener

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnAddIngredient = view.findViewById<Button>(R.id.btnAddIngredient)
        btnAddIngredient.setOnClickListener {
            Toast.makeText(requireContext(), "Bạn vừa bấm Thêm", Toast.LENGTH_SHORT).show()
        }

        val btnExplore = view.findViewById<Button>(R.id.btnMenu)
        btnExplore.setOnClickListener(
            ClickListener(requireActivity())
        )
        return view
    }

}

