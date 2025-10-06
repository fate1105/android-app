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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.email)
        val etPass  = findViewById<EditText>(R.id.pass)
        val btnLogin = findViewById<Button>(R.id.signup2Btn)   // nút "Đăng nhập"
        val tvRegister = findViewById<TextView>(R.id.loginText) // "Đăng ký"

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass  = etPass.text.toString()

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
                            btnLogin.isEnabled = true
                            btnLogin.text = "Đăng nhập"
                            Toast.makeText(this@LoginActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                            // 👉 Mở MainActivity & clear back stack
                            val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            // finish() không cần vì CLEAR_TASK đã dọn stack
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
