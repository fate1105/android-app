package com.example.angiday.ui.profile

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.angiday.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class SettingsActivity : AppCompatActivity() {

    private val prefs by lazy {
        getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Back arrow
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Views
        val swNotifications = findViewById<MaterialSwitch>(R.id.swNotifications)
        val swDarkMode = findViewById<MaterialSwitch>(R.id.swDarkMode)
        val edtLanguage = this.findViewById<MaterialAutoCompleteTextView>(R.id.edtLanguage)
        val btnClearCache = findViewById<MaterialButton>(R.id.btnClearCache)

        // Load saved states
        swNotifications.isChecked = prefs.getBoolean("notifications", true)
        swDarkMode.isChecked = prefs.getBoolean("dark_mode", false)

        // Languages
        val langs = listOf("Tiếng Việt", "English")
        edtLanguage.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, langs))
        val savedLang = prefs.getString("language", langs.first()) ?: langs.first()
        edtLanguage.setText(savedLang, false)

        // Save listeners
        swNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications", isChecked).apply()
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
            Toast.makeText(this, "Đã chọn: $v", Toast.LENGTH_SHORT).show()
            // TODO: nếu muốn đổi ngôn ngữ runtime, cần áp dụng lại Locale (nâng cấp sau)
        }

        btnClearCache.setOnClickListener {
            // Demo: bạn có thể clear Glide cache, Room, hoặc DataStore tuỳ app
            Toast.makeText(this, "Đã xoá bộ nhớ đệm (demo)", Toast.LENGTH_SHORT).show()
        }
    }
}
