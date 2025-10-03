package com.example.angiday.ui.setup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.angiday.R
import android.widget.*
import com.example.angiday.ui.main.MainActivity

class SetupInfoActivity : AppCompatActivity() {

    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var tasteSpinner: Spinner
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_info)

        heightInput = findViewById(R.id.heightInput)
        weightInput = findViewById(R.id.weightInput)
        tasteSpinner = findViewById(R.id.tasteSpinner)
        saveBtn = findViewById(R.id.saveBtn)

        saveBtn.setOnClickListener {
            val height = heightInput.text.toString()
            val weight = weightInput.text.toString()
            val taste = tasteSpinner.selectedItem.toString()

            // Lưu SharedPreferences (hoặc SQLite)
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            prefs.edit()
                .putString("height", height)
                .putString("weight", weight)
                .putString("taste", taste)
                .apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
