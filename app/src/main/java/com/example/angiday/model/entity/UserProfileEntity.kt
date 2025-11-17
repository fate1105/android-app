package com.example.angiday.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profiles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class UserProfileEntity(
    @PrimaryKey val userId: Long,
    val name: String,
    val height: Float? = null,
    val weight: Float? = null,
    val spicyLevel: Int? = 2,
    val preferMeat: Int? = 1,
    val preferVeg: Int? = 1,
    val allergies: String? = null

)