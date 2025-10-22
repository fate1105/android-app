package com.example.angiday.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

class FoodSuggestionService : Service() {
    private var job: Job? = null
    private val foods = listOf("Phở bò", "Bún chả", "Cơm tấm", "Bánh mì", "Mì xào", "Gỏi cuốn", "Hủ tiếu")

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(5000) // mỗi 5 giây gửi món mới
                val randomFood = foods.random()

                val broadcast = Intent("NEW_FOOD_SUGGESTED")
                broadcast.putExtra("foodName", randomFood)
                sendBroadcast(broadcast)
                Log.d("FoodService", "Đã gửi món ăn: $randomFood")
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
