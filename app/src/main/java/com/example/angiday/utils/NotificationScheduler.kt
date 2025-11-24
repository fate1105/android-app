package com.example.angiday.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationScheduler {

    private const val MAX_NOTIFS = 5

    fun scheduleDailyNotifications(context: Context) {
        val prefs = context.getSharedPreferences("notif_prefs", Context.MODE_PRIVATE)
        val times = prefs.getInt("times_per_day", 1).coerceIn(1, 4)
        val startH = prefs.getInt("start_hour", 8)
        val startM = prefs.getInt("start_minute", 0)
        val endH = prefs.getInt("end_hour", 20)
        val endM = prefs.getInt("end_minute", 0)

        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Hủy cũ
        repeat(MAX_NOTIFS) { i ->
            val pi = PendingIntent.getBroadcast(
                context, i,
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarm.cancel(pi)
        }

        val startTotal = startH * 60 + startM
        val endTotal = endH * 60 + endM
        val totalMinutes = if (endTotal >= startTotal) endTotal - startTotal else (1440 + endTotal - startTotal)
        val interval = if (times == 1) totalMinutes / 2 else totalMinutes / (times - 1)

        repeat(times) { i ->
            var minutesFromStart = if (times == 1) interval else i * interval
            var targetMinutes = (startTotal + minutesFromStart) % 1440
            var hour = targetMinutes / 60
            var minute = targetMinutes % 60

            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val pending = PendingIntent.getBroadcast(
                context, i,
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarm.setExact(
                    AlarmManager.RTC_WAKEUP,
                    cal.timeInMillis,
                    pending
                )
            } catch (e: SecurityException) {
                // fallback nếu Android 12 không cho exact
                alarm.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    cal.timeInMillis,
                    pending
                )
            }
        }
    }
    fun cancelDailyNotifications(context: Context) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Hủy toàn bộ PendingIntent cũ
        repeat(MAX_NOTIFS) { i ->
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                i,
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarm.cancel(pendingIntent)
        }
    }

}