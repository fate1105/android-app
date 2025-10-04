package com.example.angiday.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.angiday.R
import com.example.angiday.ui.main.fragment.HomeFragment
import com.example.angiday.ui.main.fragment.MenuFragment
import com.example.angiday.ui.main.fragment.ProfileFragment
import com.example.angiday.ui.main.fragment.SettingsFragment
import com.example.angiday.ui.main.fragment.WheelFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Load fragment mặc định (Home)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
                R.id.nav_menu -> loadFragment(MenuFragment())
                R.id.nav_wheel -> loadFragment(WheelFragment())
                R.id.nav_setting -> loadFragment(SettingsFragment())
            }
            true
        }

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun setBottomNavSelected(itemId: Int) {
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = itemId
    }


}
