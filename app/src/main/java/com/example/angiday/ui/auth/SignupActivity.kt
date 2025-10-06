package com.example.angiday.ui.auth

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
import com.example.angiday.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private val vm: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        val etEmail     = findViewById<EditText>(R.id.email)
        val etPassword  = findViewById<EditText>(R.id.pass)
        val etPassword2 = findViewById<EditText>(R.id.pass2)
        val btnRegister = findViewById<Button>(R.id.signupBtn)
        val tvLogin     = findViewById<TextView>(R.id.loginText)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass  = etPassword.text.toString()
            val pass2 = etPassword2.text.toString()

            if (email.isEmpty()) {
                etEmail.error = "Nhập email"; return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email không hợp lệ"; return@setOnClickListener
            }
            if (pass.length < 6) {
                etPassword.error = "Mật khẩu ≥ 6 ký tự"; return@setOnClickListener
            }
            if (pass != pass2) {
                etPassword2.error = "Mật khẩu nhập lại không khớp"; return@setOnClickListener
            }

            val name = email.substringBefore("@")
            vm.register(
                name = name,
                email = email,
                password = pass,
                hashPassword = false
            )
        }

        // Điều hướng sang màn hình đăng nhập
        tvLogin.setOnClickListener {
            finish() // hoặc startActivity(Intent(this, LoginActivity::class.java))
        }

        // Quan sát UI state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.ui.collect { state ->
                    when {
                        state.loading -> {
                            btnRegister.isEnabled = false
                            btnRegister.text = "Đang đăng ký..."
                        }
                        state.success -> {
                            btnRegister.isEnabled = true
                            btnRegister.text = "Đăng ký"
                            Toast.makeText(this@SignupActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

                            // 👉 Sau khi đăng ký xong, chuyển qua LoginActivity
                            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                            finish()
                        }
                        state.error != null -> {
                            btnRegister.isEnabled = true
                            btnRegister.text = "Đăng ký"
                            Toast.makeText(this@SignupActivity, "Lỗi: ${state.error}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

    }
}
