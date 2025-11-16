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
import com.example.angiday.session.SessionManager
import com.example.angiday.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.get(this)
        val userDao = db.userDao()
        val session = SessionManager(this)

        // -----------------------------------------------------
        // 🔥 AUTO LOGIN — nếu user đã "Remember Login"
        // -----------------------------------------------------
        if (session.isLoggedIn()) {
            lifecycleScope.launch {
                val user = userDao.getById(session.getUserId())
                if (user != null) {
                    startMain()
                    return@launch
                } else {
                    session.clear()
                }
            }
        }

        // -----------------------------------------------------
        // 🔥 Hiển thị giao diện login
        // -----------------------------------------------------
        setContentView(R.layout.activity_login)
        setupLoginUI(userDao, session)
    }


    private fun setupLoginUI(
        userDao: com.example.angiday.db.dao.UserDao,
        session: SessionManager
    ) {

        val etEmail = findViewById<EditText>(R.id.email)
        val etPass = findViewById<EditText>(R.id.pass)
        val chkRemember = findViewById<CheckBox>(R.id.chkRemember)
        val btnLogin = findViewById<Button>(R.id.signup2Btn)
        val tvRegister = findViewById<TextView>(R.id.loginText)

        // -----------------------------------------------------
        // 👉 Sự kiện Đăng nhập
        // -----------------------------------------------------
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString()

            // Validate input
            if (email.isEmpty()) {
                etEmail.error = "Vui lòng nhập email"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email không hợp lệ"
                return@setOnClickListener
            }
            if (pass.length < 6) {
                etPass.error = "Mật khẩu ≥ 6 ký tự"
                return@setOnClickListener
            }

            // Thực hiện đăng nhập
            lifecycleScope.launch {
                val user = userDao.findByEmailAndPassword(email, pass)

                if (user != null) {

                    // -----------------------------------------------------
                    // 🔥 Lưu user + trạng thái Remember Login
                    // -----------------------------------------------------
                    session.saveUser(
                        user = user,
                        remember = chkRemember.isChecked
                    )

                    Toast.makeText(
                        this@LoginActivity,
                        "Đăng nhập thành công!",
                        Toast.LENGTH_SHORT
                    ).show()

                    startMain()

                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Sai email hoặc mật khẩu!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // → Điều hướng sang đăng ký
        tvRegister.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }


    // -----------------------------------------------------
    // 👉 Điều hướng sang MainActivity
    // -----------------------------------------------------
    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
