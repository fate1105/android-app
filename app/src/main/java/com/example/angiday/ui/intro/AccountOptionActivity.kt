package com.example.angiday.ui.intro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.angiday.R
import com.example.angiday.ui.auth.LoginActivity
import com.example.angiday.ui.auth.SignupActivity

class AccountOptionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_option)

        loginBtn = findViewById(R.id.loginBtn)
        signupBtn = findViewById(R.id.signupBtn)

        loginBtn.setOnClickListener(this)
        signupBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loginBtn -> startActivity(Intent(this, LoginActivity::class.java))
            R.id.signupBtn -> startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
