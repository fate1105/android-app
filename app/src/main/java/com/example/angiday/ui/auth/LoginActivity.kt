package com.example.angiday.ui.auth   // ✅ phải đúng y như này

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.angiday.R
import com.example.angiday.ui.auth.SignupActivity
import com.example.angiday.ui.main.MainActivity
import com.example.angiday.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val vm: LoginViewModel by viewModels()

    // ✅ Ghi thông tin đăng nhập vào Internal Storage
    private fun saveLoginInfo(email: String, password: String) {
        val data = "$email|$password"
        openFileOutput("login.txt", MODE_PRIVATE).use {
            it.write(data.toByteArray())
        }
    }

    // ✅ Đọc thông tin đăng nhập đã lưu (nếu có)
    private fun readLoginInfo(): Pair<String, String>? {
        return try {
            val data = openFileInput("login.txt").bufferedReader().use { it.readText() }
            val parts = data.split("|")
            if (parts.size == 2) Pair(parts[0], parts[1]) else null
        } catch (e: Exception) {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Kiểm tra đăng nhập tự động trước khi hiển thị layout
        val savedLogin = readLoginInfo()
        if (savedLogin != null) {
            val (email, password) = savedLogin
            Toast.makeText(this, "Đăng nhập tự động: $email", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            return
        }

        // Bình thường nếu chưa có file đăng nhập
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.email)
        val etPass = findViewById<EditText>(R.id.pass)
        val btnLogin = findViewById<Button>(R.id.signup2Btn)   // nút "Đăng nhập"
        val tvRegister = findViewById<TextView>(R.id.loginText) // "Đăng ký"

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email không hợp lệ"; return@setOnClickListener
            }
            if (pass.length < 6) {
                etPass.error = "Mật khẩu ≥ 6 ký tự"; return@setOnClickListener
            }

            vm.login(email, pass, hashPassword = false)
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.ui.collect { state ->
                    when {
                        state.loading -> {
                            btnLogin.isEnabled = false
                            btnLogin.text = "Đang đăng nhập..."
                        }
                        state.success -> {
                            // ✅ Khi đăng nhập thành công → lưu lại thông tin
                            saveLoginInfo(etEmail.text.toString(), etPass.text.toString())

                            btnLogin.isEnabled = true
                            btnLogin.text = "Đăng nhập"
                            Toast.makeText(
                                this@LoginActivity,
                                "Đăng nhập thành công!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // 👉 Mở MainActivity & clear back stack
                            val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                        }
                        state.error != null -> {
                            btnLogin.isEnabled = true
                            btnLogin.text = "Đăng nhập"
                            Toast.makeText(this@LoginActivity, state.error, Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            btnLogin.isEnabled = true
                            btnLogin.text = "Đăng nhập"
                        }
                    }
                }
            }
        }
    }
}
