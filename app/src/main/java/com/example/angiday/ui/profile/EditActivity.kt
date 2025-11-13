package com.example.angiday.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pf)

        // 🔙 Toolbar quay lại
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val edtName = findViewById<TextInputEditText>(R.id.edtName)
        val edtEmail = findViewById<TextInputEditText>(R.id.edtEmail)
        val edtPassword = findViewById<TextInputEditText>(R.id.edtPassword)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)

        val db = AppDatabase.get(this)
        val userDao = db.userDao()

        // ✅ Đọc thông tin đăng nhập đã lưu trong file login.txt
        val loginInfo = readLoginInfo()
        if (loginInfo == null) {
            Toast.makeText(this, "Không tìm thấy thông tin đăng nhập!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val (email, password) = loginInfo

        // ✅ Lấy thông tin người dùng thực từ DB
        lifecycleScope.launch {
            val user = userDao.findByEmailAndPassword(email, password)
            if (user != null) {
                edtName.setText(user.name)
                edtEmail.setText(user.email)
            } else {
                Toast.makeText(this@EditActivity, "Không tìm thấy người dùng trong database!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // ✅ Khi nhấn Lưu
        btnSave.setOnClickListener {
            val name = edtName.text.toString().trim()
            val newEmail = edtEmail.text.toString().trim()
            val newPassword = edtPassword.text.toString().trim()

            if (name.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = userDao.findByEmailAndPassword(email, password)
                if (user != null) {
                    val updated = user.copy(
                        name = name,
                        email = newEmail,
                        password = if (newPassword.isNotEmpty()) newPassword else user.password
                    )
                    userDao.update(updated)

                    // ✅ Cập nhật lại file login.txt nếu email/password thay đổi
                    saveLoginInfo(newEmail, updated.password)

                    Toast.makeText(this@EditActivity, "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditActivity, "Không thể cập nhật người dùng!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // === 🔹 HÀM PHỤ ===

    private fun readLoginInfo(): Pair<String, String>? {
        return try {
            val data = openFileInput("login.txt").bufferedReader().use { it.readText() }
            val parts = data.split("|")
            if (parts.size == 2) Pair(parts[0], parts[1]) else null
        } catch (e: Exception) {
            null
        }
    }

    private fun saveLoginInfo(email: String, password: String) {
        val data = "$email|$password"
        openFileOutput("login.txt", MODE_PRIVATE).use {
            it.write(data.toByteArray())
        }
    }
}
