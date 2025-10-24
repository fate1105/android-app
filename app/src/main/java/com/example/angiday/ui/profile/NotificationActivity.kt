package com.example.angiday.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.angiday.R
import com.google.android.material.appbar.MaterialToolbar
import com.example.angiday.ui.profile.adapter.NotificationAdapter

class NotificationActivity : AppCompatActivity() {

    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Toolbar quay lại
        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val rv = findViewById<RecyclerView>(R.id.rvNotifications)
        rv.layoutManager = LinearLayoutManager(this)

        // Lấy dữ liệu đã lưu (SharedPreferences)
        val prefs = getSharedPreferences("food_notifications", MODE_PRIVATE)
        val allNoti = prefs.getStringSet("list", emptySet())?.toList()?.reversed() ?: emptyList()
        adapter = NotificationAdapter(allNoti)
        rv.adapter = adapter

        adapter = NotificationAdapter(allNoti)
        rv.adapter = adapter
    }
}