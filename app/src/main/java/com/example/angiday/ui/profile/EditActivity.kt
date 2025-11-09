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

        // Toolbar quay lại
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val edtName = findViewById<TextInputEditText>(R.id.edtName)
        val edtEmail = findViewById<TextInputEditText>(R.id.edtEmail)
        val edtPassword = findViewById<TextInputEditText>(R.id.edtPassword)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)

        val session = SessionManager(this)
        val db = AppDatabase.get(this)
        val userDao = db.userDao()
        val userId = session.getUserId()

        // Hiển thị thông tin hiện tại
        lifecycleScope.launch {
            val user = userDao.getUserById(userId)
            user?.let {
                edtName.setText(it.name)
                edtEmail.setText(it.email)
            }
        }

        // Khi nhấn lưu
        btnSave.setOnClickListener {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = userDao.getUserById(userId)
                if (user != null) {
                    val updated = user.copy(
                        name = name,
                        email = email,
                        password = if (password.isNotEmpty()) password else user.password
                    )
                    userDao.update(updated)
                    Toast.makeText(this@EditActivity, "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
