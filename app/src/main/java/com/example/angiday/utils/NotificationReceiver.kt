package com.example.angiday.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.angiday.db.AppDatabase

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        // Chạy DB trong background thread
        CoroutineScope(Dispatchers.IO).launch {

            val db = AppDatabase.get(context)
            val food = db.foodDao().getRandomFood()

            val title = food?.food?.title ?: "Món ngon hôm nay 😋"

            NotificationHelper.showMealSuggestion(context, title)
        }
    }
}
