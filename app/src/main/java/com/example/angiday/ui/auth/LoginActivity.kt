package com.example.angiday.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.angiday.R
import com.example.angiday.ui.main.MainActivity
import com.example.angiday.ui.auth.SignupActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Khai báo listener chung
        val listener = View.OnClickListener { v ->
            when (v?.id) {
                R.id.signup2Btn -> startActivity(Intent(this, MainActivity::class.java))
                R.id.loginText -> startActivity(Intent(this, SignupActivity::class.java))
            }
        }

        // Gán listener cho nhiều view
        val loginBtn = findViewById<Button>(R.id.signup2Btn)
        val signupBtn = findViewById<TextView>(R.id.loginText)

        loginBtn.setOnClickListener(listener)
        signupBtn.setOnClickListener(listener)

    }
}