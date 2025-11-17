package com.example.angiday.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPassword2: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        initViews()
        setupListeners()
        observeViewModel()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.email)
        etPassword = findViewById(R.id.pass)
        etPassword2 = findViewById(R.id.pass2)
        btnRegister = findViewById(R.id.signupBtn)
        tvLogin = findViewById(R.id.loginText)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener { handleRegister() }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.btnSupport).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:naml75803@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ đăng ký tài khoản")
                putExtra(Intent.EXTRA_TEXT, "Xin chào, tôi cần giúp đỡ về việc đăng ký...")
            }
            startActivity(Intent.createChooser(intent, "Gửi email"))
        }
    }

    private fun handleRegister() {
        val email = etEmail.text.toString().trim()
        val pass = etPassword.text.toString()
        val pass2 = etPassword2.text.toString()

        // Validate
        if (email.isEmpty()) {
            etEmail.error = "Nhập email"; return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email không hợp lệ"; return
        }
        if (pass.length < 6) {
            etPassword.error = "Mật khẩu ≥ 6 ký tự"; return
        }
        if (pass != pass2) {
            etPassword2.error = "Mật khẩu không khớp"; return
        }

        // Tên tạm = email prefix (có thể để trống, user tự nhập ở Setup)
        val tempName = email.substringBefore("@").takeIf { it.isNotBlank() } ?: "User"

        vm.register(
            name = tempName,
            email = email,
            password = pass,
            hashPassword = false
        )
    }

    private fun observeViewModel() {
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

                            // LẤY userId từ ViewModel (giả sử có)
                            val userId = state.userId

                            // → CHUYỂN QUA SETUP PROFILE
                            val intent = Intent(this@SignupActivity, SetupProfileActivity::class.java).apply {
                                putExtra("user_id", userId)
                            }
                            startActivity(intent)
                            finish()
                        }
                        state.error != null -> {
                            btnRegister.isEnabled = true
                            btnRegister.text = "Đăng ký"


                            Log.e("REGISTER_ERROR", "Lỗi: ${state.error}")

                        }


                    }
                }
            }
        }
    }
}