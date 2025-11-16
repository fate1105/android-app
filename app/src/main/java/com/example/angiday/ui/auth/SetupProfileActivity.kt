package com.example.angiday.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserProfileEntity
import com.example.angiday.ui.main.MainActivity
import kotlinx.coroutines.launch
import com.example.angiday.databinding.ActivitySetupProfileBinding


class SetupProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupProfileBinding
    private lateinit var db: AppDatabase
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.Companion.get(this)
        userId = intent.getLongExtra("user_id", -1)
        if (userId == -1L) finish()

        binding.btnSave.setOnClickListener { validateAndSave() }
    }

    private fun validateAndSave() {
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.etName.error = "Nhập tên"
            return
        }

        val heightStr = binding.etHeight.text.toString()
        val weightStr = binding.etWeight.text.toString()

        val height = heightStr.toFloatOrNull()
        val weight = weightStr.toFloatOrNull()

        if (height != null && height <= 0) {
            binding.etHeight.error = "Chiều cao > 0"
            return
        }
        if (weight != null && weight <= 0) {
            binding.etWeight.error = "Cân nặng > 0"
            return
        }

        val spicyLevel = when (binding.rgSpicy.checkedRadioButtonId) {
            R.id.rb_low -> 1
            R.id.rb_medium -> 3
            R.id.rb_high -> 5
            else -> 2
        }

        val profile = UserProfileEntity(
            userId = userId,
            name = name,
            height = height,
            weight = weight,
            spicyLevel = spicyLevel,
            preferMeat = true, // Có thể thêm checkbox sau
            preferVeg = true,
            allergies = emptyList() // Có thể thêm input sau
        )

        lifecycleScope.launch {
            db.userProfileDao().insert(profile)
            startActivity(Intent(this@SetupProfileActivity, MainActivity::class.java))
            finish()
        }
    }
}