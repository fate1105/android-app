package com.example.angiday.ui.profile

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.angiday.R
import com.example.angiday.utils.NotificationScheduler

class NotificationActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnEnd: Button
    private lateinit var spinner: Spinner
    private lateinit var btnSave: Button

    private var startHour = 8
    private var startMinute = 0
    private var endHour = 20
    private var endMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        btnStart = findViewById(R.id.btn_pick_start_time)
        btnEnd = findViewById(R.id.btn_pick_end_time)
        spinner = findViewById(R.id.spinner_times_per_day)
        btnSave = findViewById(R.id.btn_save)

        loadSavedSettings()

        btnStart.setOnClickListener { showTimePicker(true) }
        btnEnd.setOnClickListener { showTimePicker(false) }

        btnSave.setOnClickListener {
            saveSettings()
            Toast.makeText(this, "Đã lưu! Thông báo sẽ được lên lịch lại.", Toast.LENGTH_SHORT).show()
            NotificationScheduler.scheduleDailyNotifications(this)
            finish()
        }
    }

    private fun showTimePicker(isStart: Boolean) {
        val hour = if (isStart) startHour else endHour
        val minute = if (isStart) startMinute else endMinute

        TimePickerDialog(this, { _, h, m ->
            if (isStart) {
                startHour = h; startMinute = m
                btnStart.text = String.format("%02d:%02d", h, m)
            } else {
                endHour = h; endMinute = m
                btnEnd.text = String.format("%02d:%02d", h, m)
            }
        }, hour, minute, true).show()
    }

    private fun loadSavedSettings() {
        val prefs = getSharedPreferences("notif_prefs", MODE_PRIVATE)
        val timesPerDay = prefs.getInt("times_per_day", 1)
        startHour = prefs.getInt("start_hour", 8)
        startMinute = prefs.getInt("start_minute", 0)
        endHour = prefs.getInt("end_hour", 20)
        endMinute = prefs.getInt("end_minute", 0)

        spinner.setSelection(timesPerDay.coerceAtMost(3)) // 0-3
        btnStart.text = String.format("%02d:%02d", startHour, startMinute)
        btnEnd.text = String.format("%02d:%02d", endHour, endMinute)
    }

    private fun saveSettings() {
        val selectedTimes = spinner.selectedItemPosition + 1
        getSharedPreferences("notif_prefs", MODE_PRIVATE).edit()
            .putInt("times_per_day", selectedTimes)
            .putInt("start_hour", startHour)
            .putInt("start_minute", startMinute)
            .putInt("end_hour", endHour)
            .putInt("end_minute", endMinute)
            .apply()
    }
}