package com.example.angiday.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.databinding.ActivityEditPfBinding
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserProfileEntity
import com.example.angiday.session.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPfBinding
    private lateinit var db: AppDatabase
    private lateinit var session: SessionManager
    private val gson = Gson()
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.get(this)
        session = SessionManager(this)
        userId = session.getUserId()

        if (userId == -1L) {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Toolbar: back
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Load data
        loadUserData()

        // Save
        binding.btnSave.setOnClickListener { validateAndSave() }
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            val user = db.userDao().getById(userId)
            val profile = db.userProfileDao().getByUserId(userId)

            if (user == null) {
                Toast.makeText(this@EditActivity, "Không tìm thấy user!", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            with(binding) {
                // User
                edtName.setText(user.name)
                edtEmail.setText(user.email)

                // Profile
                profile?.let {
                    edtHeight.setText(it.height?.toString() ?: "")
                    edtWeight.setText(it.weight?.toString() ?: "")

                    // Spicy
                    when (it.spicyLevel) {
                        1 -> binding.rgSpicy.check(R.id.rb_low)   // Không cay
                        5 -> binding.rgSpicy.check(R.id.rb_high)  // Cay
                        else -> binding.rgSpicy.check(R.id.rb_low)
                    }

                    // === DIET (Ăn mặn / Ăn chay) ===
                    if (it.preferMeat == 1) {
                        rgDiet.check(R.id.rb_meat)
                    } else {
                        rgDiet.check(R.id.rb_veg)
                    }

                    // Allergies
                    val allergiesList = gson.fromJson(it.allergies, Array<String>::class.java)?.toList() ?: emptyList()
                    edtAllergies.setText(allergiesList.joinToString(", "))
                }
            }
        }
    }

    private fun validateAndSave() {
        // Name + Email
        val name = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nhập tên & email!", Toast.LENGTH_SHORT).show()
            return
        }

        // Height + Weight
        val height = binding.edtHeight.text.toString().toFloatOrNull()
        val weight = binding.edtWeight.text.toString().toFloatOrNull()
        if (height != null && height <= 0) {
            binding.edtHeight.error = "Chiều cao > 0"
            return
        }
        if (weight != null && weight <= 0) {
            binding.edtWeight.error = "Cân nặng > 0"
            return
        }

        // Spicy
        val spicyLevel = when (binding.rgSpicy.checkedRadioButtonId) {
            R.id.rb_low -> 1   // Không cay
            R.id.rb_high -> 5  // Cay
            else -> 1
        }

        // === DIET ===
        val (preferMeat, preferVeg) = when (binding.rgDiet.checkedRadioButtonId) {
            R.id.rb_meat -> 1 to 0
            R.id.rb_veg -> 0 to 1
            else -> 1 to 0
        }
        // Allergies
        val allergiesInput = binding.edtAllergies.text.toString().trim()
        val allergiesJson = if (allergiesInput.isBlank()) null
        else gson.toJson(allergiesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() })

        // Password
        val newPass = binding.edtPassword.text.toString()

        lifecycleScope.launch {
            // Update User
            val user = db.userDao().getById(userId) ?: return@launch
            val updatedUser = user.copy(
                name = name,
                email = email,
                password = if (newPass.isNotEmpty()) newPass else user.password
            )
            db.userDao().update(updatedUser)
            session.saveUser(updatedUser, true)

            // Update / Insert Profile
            val existingProfile = db.userProfileDao().getByUserId(userId)
            val profile = (existingProfile ?: UserProfileEntity(
                userId = userId,
                name = name,  // <-- DÙNG name ĐÃ VALIDATE TRƯỚC
                height = null,
                weight = null,
                spicyLevel = 3,
                preferMeat = 1,
                preferVeg = 1,
                allergies = null
            )).copy(
                name = name,
                height = height,
                weight = weight,
                spicyLevel = spicyLevel,
                preferMeat = preferMeat,
                preferVeg = preferVeg,
                allergies = allergiesJson
            )
            db.userProfileDao().insert(profile) // onConflict = REPLACE

            Toast.makeText(this@EditActivity, "Đã lưu!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}