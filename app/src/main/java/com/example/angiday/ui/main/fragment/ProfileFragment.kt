package com.example.angiday.ui.main.fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.angiday.R
import com.example.angiday.ui.profile.EditActivity
import com.example.angiday.ui.profile.FoodLvActivity
import com.example.angiday.ui.profile.FoodHistoryActivity
import com.example.angiday.ui.share.ShareActivity
import com.example.angiday.ui.profile.CookingSeriesActivity
import com.example.angiday.ui.auth.LoginActivity
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
        // 👉 Khi người dùng bấm vào “Chia sẻ món ăn”
        rowNotice.setOnClickListener {
            val intent = Intent(requireContext(), ShareActivity::class.java)
            startActivity(intent)
        }

        rowEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditActivity::class.java)
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
        // 👉 Đăng xuất
        rowLogout.setOnClickListener {
            val sharedPref = requireContext().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()  // Xóa thông tin đăng nhập

            // Điều hướng về màn hình đăng nhập
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}
