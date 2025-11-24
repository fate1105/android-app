package com.example.angiday.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.angiday.R
import com.example.angiday.ui.main.MainActivity

object NotificationHelper {

    private const val CHANNEL_ID = "meal_suggestion_channel"
    private const val NOTIFICATION_ID = 1001

    fun showMealSuggestion(context: Context, mealName: String) {
        val nm = context.getSystemService(NotificationManager::class.java)

        // Tạo channel chỉ 1 lần (từ Android O)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.getNotificationChannel(CHANNEL_ID) ?: nm.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Gợi ý bữa ăn", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Thông báo gợi ý món ăn mỗi ngày"
                    enableLights(true)
                    lightColor = Color.GREEN
                }
            )
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Gợi ý món ăn hôm nay 🍱")
            .setContentText("Hôm nay thử món '$mealName' nhé?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        nm.notify(NOTIFICATION_ID, notification)
    }
}