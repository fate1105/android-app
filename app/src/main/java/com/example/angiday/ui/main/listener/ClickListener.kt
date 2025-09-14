package com.example.angiday.ui.main.listener

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.angiday.R
import com.example.angiday.ui.main.MainActivity
import com.example.angiday.ui.main.fragment.MenuFragment

class ClickListener(private val activity: FragmentActivity) : View.OnClickListener {
    override fun onClick(v: View?) {
        // Chuyển Fragment khi bấm nút
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MenuFragment())
            .addToBackStack(null)
            .commit()
        (activity as? MainActivity)?.setBottomNavSelected(R.id.nav_menu)

    }
}
