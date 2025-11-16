package com.example.angiday.ui.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.session.SessionManager
import com.example.angiday.ui.auth.LoginActivity
import com.example.angiday.ui.profile.*
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.textView6)
        tvEmail = view.findViewById(R.id.textView9)

        val rowNotice = view.findViewById<ConstraintLayout>(R.id.rowNotice)
        val rowEditProfile = view.findViewById<ConstraintLayout>(R.id.rowEditProfile)
        val rowFavorite = view.findViewById<ConstraintLayout>(R.id.rowFavorite)
        val rowHistory = view.findViewById<ConstraintLayout>(R.id.rowHistory)
        val rowMyProfile = view.findViewById<ConstraintLayout>(R.id.rowMyProfile)
        val rowLogout = view.findViewById<ConstraintLayout>(R.id.rowLogout)

        // 👉 Các điều hướng
        rowNotice.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
        rowEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditActivity::class.java))
        }
        rowFavorite.setOnClickListener {
            startActivity(Intent(requireContext(), FoodFavoriteActivity::class.java))
        }
        rowHistory.setOnClickListener {
            startActivity(Intent(requireContext(), FoodHistoryActivity::class.java))
        }
        rowMyProfile.setOnClickListener {
            startActivity(Intent(requireContext(), MyProfileActivity::class.java))
        }

        // 👉 Đăng xuất
        rowLogout.setOnClickListener {
            val session = SessionManager(requireContext())
            session.clearUserButKeepRemember()


            Toast.makeText(requireContext(), "Đã đăng xuất!", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val session = SessionManager(requireContext())
        val userId = session.getUserId()

        if (userId == -1L) {
            tvName.text = "Khách"
            tvEmail.text = "Vui lòng đăng nhập"
            return
        }

        val userDao = AppDatabase.get(requireContext()).userDao()

        viewLifecycleOwner.lifecycleScope.launch {
            val user = userDao.getUserById(userId)

            if (user != null) {
                tvName.text = user.name
                tvEmail.text = user.email
            } else {
                tvName.text = "Khách"
                tvEmail.text = "Vui lòng đăng nhập"
            }
        }
    }
}
