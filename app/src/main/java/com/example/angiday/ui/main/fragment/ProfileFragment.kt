package com.example.angiday.ui.main.fragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.angiday.R
import com.example.angiday.ui.profile.Edit_pfActivity
import com.example.angiday.ui.profile.FoodLvActivity
import com.example.angiday.ui.profile.FoodHistoryActivity
import com.example.angiday.ui.profile.SettingsActivity
import com.example.angiday.ui.profile.CookingSeriesActivity

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Ánh xạ các row
        val rowNotice = view.findViewById<ConstraintLayout>(R.id.rowNotice)
        val rowEditProfile = view.findViewById<ConstraintLayout>(R.id.rowEditProfile)
        val rowFavorite = view.findViewById<ConstraintLayout>(R.id.rowFavorite)
        val rowHistory = view.findViewById<ConstraintLayout>(R.id.rowHistory)
        val rowCooking = view.findViewById<ConstraintLayout>(R.id.rowCooking)
        val rowLogout = view.findViewById<ConstraintLayout>(R.id.rowLogout)

        // Gắn sự kiện click
        rowNotice.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        rowEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), Edit_pfActivity::class.java)
            startActivity(intent)
        }

        rowFavorite.setOnClickListener {
            val intent = Intent(requireContext(), FoodLvActivity::class.java)
            startActivity(intent)
        }

        rowHistory.setOnClickListener {
            val intent = Intent(requireContext(), FoodHistoryActivity::class.java)
            startActivity(intent)
        }

        rowCooking.setOnClickListener {
            val intent = Intent(requireContext(), CookingSeriesActivity::class.java)
            startActivity(intent)
        }


        return view
    }
}
