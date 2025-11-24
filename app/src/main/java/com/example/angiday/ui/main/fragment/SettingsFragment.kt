package com.example.angiday.ui.main.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.angiday.R
import com.example.angiday.utils.NotificationScheduler
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class SettingsFragment : Fragment() {

    private val prefs by lazy {
        requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Views
        val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        val swNotifications = view.findViewById<MaterialSwitch>(R.id.swNotifications)
        val swDarkMode = view.findViewById<MaterialSwitch>(R.id.swDarkMode)
        val edtLanguage = view.findViewById<MaterialAutoCompleteTextView>(R.id.edtLanguage)
        val btnClearCache = view.findViewById<MaterialButton>(R.id.btnClearCache)


        // Load saved states
        swNotifications.isChecked = prefs.getBoolean("notifications", true)
        swDarkMode.isChecked = prefs.getBoolean("dark_mode", false)

        // Languages
        val langs = listOf("Tiếng Việt", "English")
        edtLanguage.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, langs)
        )
        val savedLang = prefs.getString("language", langs.first()) ?: langs.first()
        edtLanguage.setText(savedLang, false)

        // Save listeners
        swNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications", isChecked).apply()

            if (isChecked) {
                // Bật thông báo
                NotificationScheduler.scheduleDailyNotifications(requireContext())
                Toast.makeText(requireContext(), "Đã bật thông báo", Toast.LENGTH_SHORT).show()
            } else {
                // Tắt thông báo
                NotificationScheduler.cancelDailyNotifications(requireContext())
                Toast.makeText(requireContext(), "Đã tắt thông báo", Toast.LENGTH_SHORT).show()
            }
        }

        swDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        edtLanguage.setOnItemClickListener { _, _, position, _ ->
            val v = langs[position]
            prefs.edit().putString("language", v).apply()
            Toast.makeText(requireContext(), "Đã chọn: $v", Toast.LENGTH_SHORT).show()
            // TODO: đổi Locale runtime nếu muốn
        }

        btnClearCache.setOnClickListener {
            Toast.makeText(requireContext(), "Đã xoá bộ nhớ đệm (demo)", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
