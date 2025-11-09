package com.example.angiday.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_behavior")
data class UserBehaviorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Long,
    val foodId: Long,
    val behaviorType: String, // "favorite", "shared", "cooked"
    val timestamp: String? = null
)
