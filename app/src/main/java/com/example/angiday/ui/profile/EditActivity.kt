package com.example.angiday.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.session.SessionManager
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

        val userDao = AppDatabase.get(this).userDao()
        val session = SessionManager(this)

        // 🔥 lấy userId đã login
        val userId = session.getUserId()

        if (userId == -1L) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 🔥 Load thông tin user theo ID
        lifecycleScope.launch {
            val user = userDao.getById(userId)

            if (user == null) {
                Toast.makeText(this@EditActivity, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            edtName.setText(user.name)
            edtEmail.setText(user.email)
        }

        // 🔥 Lưu thay đổi
        btnSave.setOnClickListener {
            val newName = edtName.text.toString().trim()
            val newEmail = edtEmail.text.toString().trim()
            val newPassword = edtPassword.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = userDao.getById(userId)
                if (user == null) {
                    Toast.makeText(this@EditActivity, "Lỗi khi đọc user!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val updated = user.copy(
                    name = newName,
                    email = newEmail,
                    password = if (newPassword.isNotEmpty()) newPassword else user.password
                )

                userDao.update(updated)

                // 🔥 Cập nhật session khi user đổi name/email/password
                session.saveUser(updated)

                Toast.makeText(this@EditActivity, "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
