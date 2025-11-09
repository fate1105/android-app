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
import com.example.angiday.ui.profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // ánh xạ view
        val tvName = view.findViewById<TextView>(R.id.textView6)
        val tvEmail = view.findViewById<TextView>(R.id.textView9)

        val rowNotice = view.findViewById<ConstraintLayout>(R.id.rowNotice)
        val rowEditProfile = view.findViewById<ConstraintLayout>(R.id.rowEditProfile)
        val rowFavorite = view.findViewById<ConstraintLayout>(R.id.rowFavorite)
        val rowHistory = view.findViewById<ConstraintLayout>(R.id.rowHistory)
        val rowMyProfile = view.findViewById<ConstraintLayout>(R.id.rowMyProfile)
        val rowLogout = view.findViewById<ConstraintLayout>(R.id.rowLogout)

        // hiển thị thông tin người dùng
        val session = SessionManager(requireContext())
        if (session.isLoggedIn()) {
            tvName.text = session.getUserName() ?: "Người dùng"
            tvEmail.text = session.getUserEmail() ?: "Chưa có email"
        } else {
            tvName.text = "Khách"
            tvEmail.text = "Vui lòng đăng nhập"
        }

        // mở thông báo
        rowNotice.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        // sửa hồ sơ
        rowEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditActivity::class.java))
        }

        // món yêu thích
        rowFavorite.setOnClickListener {
            startActivity(Intent(requireContext(), FoodFavoriteActivity::class.java))
        }

        // lịch sử món đã nấu
        rowHistory.setOnClickListener {
            startActivity(Intent(requireContext(), FoodHistoryActivity::class.java))
        }

        // hồ sơ của tôi
        rowMyProfile.setOnClickListener {
            startActivity(Intent(requireContext(), MyProfileActivity::class.java))
        }

        // đăng xuất
        rowLogout.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }

        return view
    }
}
