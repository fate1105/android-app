package com.example.angiday.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.angiday.R
import com.google.android.material.appbar.MaterialToolbar

class Edit_pfActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_pf)
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}