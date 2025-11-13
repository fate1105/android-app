package com.example.angiday.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // ✅ Lưu thông tin đăng nhập vào file nội bộ
    private fun saveLoginInfo(email: String, password: String) {
        val data = "$email|$password"
        openFileOutput("login.txt", MODE_PRIVATE).use {
            it.write(data.toByteArray())
        }
    }

    // ✅ Đọc thông tin đăng nhập đã lưu
    private fun readLoginInfo(): Pair<String, String>? {
        return try {
            val data = openFileInput("login.txt").bufferedReader().use { it.readText() }
            val parts = data.split("|")
            if (parts.size == 2) Pair(parts[0], parts[1]) else null
        } catch (e: Exception) {
            null
        }
    }

    // ✅ Xóa file đăng nhập
    private fun clearLoginFile() {
        try {
            deleteFile("login.txt")
        } catch (_: Exception) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.get(this)
        val userDao = db.userDao()

        // ✅ Kiểm tra auto login
        val savedLogin = readLoginInfo()
        if (savedLogin != null) {
            val (email, password) = savedLogin

            lifecycleScope.launch {
                val user = userDao.findByEmailAndPassword(email, password)
                if (user != null) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Đăng nhập tự động: ${user.email}",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(this@LoginActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                    finish()
                } else {
                    clearLoginFile()
                    setContentView(R.layout.activity_login)
                    setupLoginUI(userDao)
                }
            }
        } else {
            setContentView(R.layout.activity_login)
            setupLoginUI(userDao)
        }
    }

    // ✅ Xử lý giao diện đăng nhập
    private fun setupLoginUI(userDao: com.example.angiday.db.dao.UserDao) {
        val etEmail = findViewById<EditText>(R.id.email)
        val etPass = findViewById<EditText>(R.id.pass)
        val btnLogin = findViewById<Button>(R.id.signup2Btn)   // nút "Đăng nhập"
        val tvRegister = findViewById<TextView>(R.id.loginText) // "Đăng ký"
        val cbRemember = findViewById<CheckBox>(R.id.cbRemember) // ✅ checkbox “Ghi nhớ mật khẩu”

        // ✅ Nếu có thông tin cũ thì hiển thị sẵn
        val savedLogin = readLoginInfo()
        if (savedLogin != null) {
            etEmail.setText(savedLogin.first)
            etPass.setText(savedLogin.second)
            cbRemember.isChecked = true
        }

        // 🔹 Xử lý khi nhấn nút đăng nhập
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email không hợp lệ"
                return@setOnClickListener
            }
            if (pass.length < 6) {
                etPass.error = "Mật khẩu ≥ 6 ký tự"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = userDao.findByEmailAndPassword(email, pass)
                if (user != null) {
                    // ✅ Lưu nếu người dùng tick "Ghi nhớ mật khẩu"
                    if (cbRemember.isChecked) {
                        saveLoginInfo(email, pass)
                        Toast.makeText(
                            this@LoginActivity,
                            "Đã lưu tài khoản cho lần sau",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        clearLoginFile()
                    }

                    Toast.makeText(
                        this@LoginActivity,
                        "Đăng nhập thành công!",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Sai email hoặc mật khẩu!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // 🔹 Chuyển qua trang đăng ký
        tvRegister.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
