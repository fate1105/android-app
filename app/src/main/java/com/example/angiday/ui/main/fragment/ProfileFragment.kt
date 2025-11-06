package com.example.angiday.ui.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.angiday.R
import com.example.angiday.session.SessionManager
import com.example.angiday.ui.auth.LoginActivity
import com.example.angiday.ui.profile.EditActivity
import com.example.angiday.ui.profile.FoodHistoryActivity
import com.example.angiday.ui.profile.FoodFavoriteActivity
import com.example.angiday.ui.profile.MyProfileActivity
import com.example.angiday.ui.profile.NotificationActivity

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // --- Ánh xạ các view ---
        val tvName = view.findViewById<TextView>(R.id.textView6)
        val tvEmail = view.findViewById<TextView>(R.id.textView9)

        val rowNotice = view.findViewById<ConstraintLayout>(R.id.rowNotice)
        val rowEditProfile = view.findViewById<ConstraintLayout>(R.id.rowEditProfile)
        val rowFavorite = view.findViewById<ConstraintLayout>(R.id.rowFavorite)
        val rowHistory = view.findViewById<ConstraintLayout>(R.id.rowHistory)
        val rowMyProfile = view.findViewById<ConstraintLayout>(R.id.rowMyProfile)
        val rowLogout = view.findViewById<ConstraintLayout>(R.id.rowLogout)

        // --- Hiển thị thông tin người dùng ---
        val session = SessionManager(requireContext())
        if (session.isLoggedIn()) {
            val userName = session.getUserName() ?: "Người dùng"
            val userEmail = session.getUserEmail() ?: "Chưa có email"
            tvName.text = userName
            tvEmail.text = userEmail
        } else {
            tvName.text = "Khách"
            tvEmail.text = "Vui lòng đăng nhập"
        }

        // 👉 Chia sẻ món ăn
        rowNotice.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity ::class.java))
        }

        // 👉 Sửa hồ sơ
        rowEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditActivity::class.java))
        }

        // 👉 Món yêu thích
        rowFavorite.setOnClickListener {
            startActivity(Intent(requireContext(), FoodFavoriteActivity::class.java))
        }

        // 👉 Lịch sử món đã nấu
        rowHistory.setOnClickListener {
            startActivity(Intent(requireContext(), FoodHistoryActivity::class.java))
        }

        // 👉 Hồ sơ của tôi
        rowMyProfile.setOnClickListener {
            startActivity(Intent(requireContext(), MyProfileActivity::class.java))
        }

        // 👉 Đăng xuất
        rowLogout.setOnClickListener {
            val sharedPref = requireContext().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}
