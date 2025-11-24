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

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        loadUserData()
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
                edtName.setText(user.name)
                edtEmail.setText(user.email)

                profile?.let {
                    edtHeight.setText(it.height?.toString() ?: "")
                    edtWeight.setText(it.weight?.toString() ?: "")

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

                    val allergiesList =
                        gson.fromJson(it.allergies, Array<String>::class.java)?.toList()
                            ?: emptyList()
                    edtAllergies.setText(allergiesList.joinToString(", "))
                }
            }
        }
    }

   private fun validateAndSave() {
        val name = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nhập tên & email!", Toast.LENGTH_SHORT).show()
            return
        }

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
        val allergiesJson =
            if (allergiesInput.isBlank()) null
            else gson.toJson(allergiesInput.split(",").map { it.trim() })

        // ----------- PASSWORD -----------

        val oldPass = binding.edtOldPassword.text.toString().trim()
        val newPass = binding.edtNewPassword.text.toString().trim()
        val confirmPass = binding.edtConfirmPassword.text.toString().trim()

        lifecycleScope.launch {

            val userDB = db.userDao().getById(userId)
            if (userDB == null) {
                Toast.makeText(this@EditActivity, "User không tồn tại!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Nếu nhập bất kỳ trường mật khẩu nào -> check toàn bộ
            if (oldPass.isNotEmpty() || newPass.isNotEmpty() || confirmPass.isNotEmpty()) {

                if (oldPass.isEmpty()) {
                    binding.edtOldPassword.error = "Vui lòng nhập mật khẩu hiện tại"
                    return@launch
                }
                if (oldPass != userDB.password) {
                    binding.edtOldPassword.error = "Mật khẩu hiện tại không đúng"
                    return@launch
                }

                if (newPass.isEmpty()) {
                    binding.edtNewPassword.error = "Vui lòng nhập mật khẩu mới"
                    return@launch
                }
                if (newPass == oldPass) {
                    binding.edtNewPassword.error = "Mật khẩu mới phải khác mật khẩu cũ"
                    return@launch
                }

                if (confirmPass.isEmpty()) {
                    binding.edtConfirmPassword.error = "Vui lòng nhập lại mật khẩu mới"
                    return@launch
                }
                if (confirmPass != newPass) {
                    binding.edtConfirmPassword.error = "Xác nhận mật khẩu không khớp"
                    return@launch
                }
            }

            saveUserAndProfile(
                name = name,
                email = email,
                newPassword = if (newPass.isNotEmpty()) newPass else userDB.password,
                height = height,
                weight = weight,
                spicyLevel = spicyLevel,
                preferMeat = preferMeat,
                preferVeg = preferVeg,
                allergiesJson = allergiesJson
            )
        }
    }


    private suspend fun saveUserAndProfile(
        name: String,
        email: String,
        newPassword: String,
        height: Float?,
        weight: Float?,
        spicyLevel: Int,
        preferMeat: Int,
        preferVeg: Int,
        allergiesJson: String?
    ) {
        val user = db.userDao().getById(userId) ?: return

        val updatedUser = user.copy(
            name = name,
            email = email,
            password = newPassword
        )
        db.userDao().update(updatedUser)
        session.saveUser(updatedUser, true)

        val profileOld = db.userProfileDao().getByUserId(userId)
        val profile = (profileOld ?: UserProfileEntity(
            userId = userId,
            name = name,
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

        db.userProfileDao().insert(profile)

        Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
