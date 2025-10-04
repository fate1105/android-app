package com.example.angiday.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "foods",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,                // NOT NULL, AUTOINCREMENT
    val title: String,               // NOT NULL
    val desc: String?,               // nullable
    val imageRes: Int?,              // drawable resId
    val imageUrl: String?,           // online image url
    val instructions: String?,       // nullable
    val youtubeId: String?,           // nullable
    val categoryId: Int?
)


