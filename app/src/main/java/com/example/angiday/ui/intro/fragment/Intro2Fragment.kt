package com.example.angiday.ui.intro.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.angiday.R
import com.example.angiday.ui.auth.LoginActivity
import com.example.angiday.ui.auth.SignupActivity

class Intro2Fragment : Fragment(R.layout.fragment_intro2) { // dùng layout fragment_intro2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnYes = view.findViewById<Button>(R.id.btnYes)
        val btnNo = view.findViewById<Button>(R.id.btnNo)

        btnYes.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        btnNo.setOnClickListener {
            startActivity(Intent(requireContext(), SignupActivity::class.java))
            requireActivity().finish()
        }
    }
}
