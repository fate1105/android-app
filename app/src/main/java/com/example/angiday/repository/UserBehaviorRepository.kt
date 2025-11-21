package com.example.angiday.repository

import com.example.angiday.db.AppDatabase

class UserBehaviorRepository(private val db: AppDatabase) {

    suspend fun getCookedDays(userId: Int): List<String> {
        return db.userBehaviorDao().getCookedDays(userId)
    }
}
