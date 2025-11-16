package com.example.angiday.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: Long,
    val name: String,
    val height: Float? = null,
    val weight: Float? = null,
    val spicyLevel: Int = 2,
    val preferMeat: Boolean = true,
    val preferVeg: Boolean = true,
    val allergies: List<String> = emptyList()
)