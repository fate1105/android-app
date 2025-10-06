package com.example.angiday.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.angiday.R
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

//        val emailEt = findViewById<EditText>(R.id.email)
//        val passEt = findViewById<EditText>(R.id.pass)
//        val pass2Et = findViewById<EditText>(R.id.pass2)
//        val signupBtn = findViewById<TextView>(R.id.signupBtn)
//        val loginText = findViewById<TextView>(R.id.loginText)
//
//        signupBtn.setOnClickListener {
//            val email = emailEt.text.toString().trim()
//            val pass = passEt.text.toString().trim()
//            val pass2 = pass2Et.text.toString().trim()
//
//            if (email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
//                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            if (pass != pass2) {
//                Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            lifecycleScope.launch {
//                val dao = AppDatabase.get(this@SignupActivity).userDao()
//
//                // Kiểm tra email đã tồn tại chưa
//                val existing = withContext(Dispatchers.IO) {
//                    dao.getUserByEmail(email)
//                }
//                if (existing != null) {
//                    Toast.makeText(this@SignupActivity, "Email đã được sử dụng!", Toast.LENGTH_SHORT).show()
//                    return@launch
//                }
//
//                // Lưu người dùng mới
//                val newUser = UserEntity(name = "Người dùng", email = email, password = pass)
//                withContext(Dispatchers.IO) {
//                    dao.insertUser(newUser)
//                }
//
//                Toast.makeText(this@SignupActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
//                finish()
//            }
//        }

//        loginText.setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }
    }
}
