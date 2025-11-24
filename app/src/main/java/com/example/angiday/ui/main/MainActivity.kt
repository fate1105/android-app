package com.example.angiday.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.angiday.R
import com.example.angiday.ui.explore.ExploreFragment
import com.example.angiday.ui.main.fragment.*
import com.example.angiday.utils.NotificationScheduler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NotificationScheduler.scheduleDailyNotifications(this)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        requestNotificationPermission() // xin quyền thông báo (Android 13+)

        // load fragment mặc định (Home)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNav.selectedItemId = R.id.nav_home
        }
        val fabHome = findViewById<MaterialCardView>(R.id.btn_home)

        fabHome.setOnClickListener {
            bottomNav.selectedItemId = R.id.nav_home   // để đồng bộ trạng thái
            loadFragment(HomeFragment())
        }

        // điều hướng bottom navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> loadFragment(ProfileFragment())
                R.id.nav_menu -> loadFragment(MenuFragment())
                R.id.nav_explore -> loadFragment(ExploreFragment())
                R.id.nav_setting -> loadFragment(SettingsFragment())
            }
            true
        }
    }

    // chuyển fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // thay đổi tab bottom nav từ fragment khác
    fun setBottomNavSelected(itemId: Int) {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = itemId
    }

    // xin quyền thông báo cho Android 13+
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}
