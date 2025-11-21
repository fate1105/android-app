package com.example.angiday.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserProfileEntity
import com.example.angiday.ui.main.MainActivity
import com.example.angiday.databinding.ActivitySetupProfileBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SetupProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupProfileBinding
    private lateinit var db: AppDatabase
    private var userId: Long = -1
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.get(this)
        userId = intent.getLongExtra("user_id", -1)
        if (userId == -1L) finish()

        binding.btnSave.setOnClickListener { validateAndSave() }
    }

    private fun validateAndSave() {
        // === Tên ===
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.etName.error = "Nhập họ tên"
            return
        }

        // === Chiều cao ===
        val heightStr = binding.etHeight.text.toString()
        val height = heightStr.toFloatOrNull()
        if (height == null || height <= 0) {
            binding.etHeight.error = "Chiều cao > 0"
            return
        }

        // === Cân nặng ===
        val weightStr = binding.etWeight.text.toString()
        val weight = weightStr.toFloatOrNull()
        if (weight == null || weight <= 0) {
            binding.etWeight.error = "Cân nặng > 0"
            return
        }

        // === Khẩu vị cay ===
        val spicyLevel = when (binding.rgSpicy.checkedRadioButtonId) {
            R.id.rb_low -> 1
            R.id.rb_medium -> 3
            R.id.rb_high -> 5
            else -> 3 // default
        }

        // === Thích thịt / rau ===
        val preferMeat = if (binding.cbPreferMeat.isChecked) 1 else 0
        val preferVeg = if (binding.cbPreferVeg.isChecked) 1 else 0

        // === Dị ứng → JSON string ===
        val allergiesInput = binding.etAllergies.text.toString().trim()
        val allergiesJson = if (allergiesInput.isBlank()) {
            null
        } else {
            val list = allergiesInput
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            gson.toJson(list)
        }

        // === Tạo entity ===
        val profile = UserProfileEntity(
            userId = userId,
            name = name,
            height = height,
            weight = weight,
            spicyLevel = spicyLevel,
            preferMeat = preferMeat,
            preferVeg = preferVeg,
            allergies = allergiesJson
        )

        // === Lưu DB + chuyển màn ===
        lifecycleScope.launch {
            db.userProfileDao().insert(profile)
            startActivity(Intent(this@SetupProfileActivity, MainActivity::class.java))
            finish()
        }
    }
}