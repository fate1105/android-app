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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.get(this)
        val userDao = db.userDao()

        // Auto login nếu đã có userId
        val pref = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        val savedUserId = pref.getLong("user_id", -1)

        if (savedUserId != -1L) {
            lifecycleScope.launch {
                val user = userDao.getById(savedUserId)
                if (user != null) {
                    startMain()
                    return@launch
                }
            }
        }

        setContentView(R.layout.activity_login)
        setupLoginUI(userDao)
    }

    private fun setupLoginUI(userDao: com.example.angiday.db.dao.UserDao) {
        val etEmail = findViewById<EditText>(R.id.email)
        val etPass = findViewById<EditText>(R.id.pass)
        val btnLogin = findViewById<Button>(R.id.signup2Btn)
        val tvRegister = findViewById<TextView>(R.id.loginText)

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
                    // Lưu userId để đăng nhập lần sau
                    val pref = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
                    pref.edit()
                        .putLong("user_id", user.id)
                        .apply()

                    Toast.makeText(this@LoginActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    startMain()
                } else {
                    Toast.makeText(this@LoginActivity, "Sai email hoặc mật khẩu!", Toast.LENGTH_LONG).show()
                }
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
