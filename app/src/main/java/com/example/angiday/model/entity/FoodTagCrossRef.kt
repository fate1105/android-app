package com.example.angiday.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "food_tag_crossref",
    primaryKeys = ["foodId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = FoodEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FoodTagCrossRef(
    val foodId: Long,
    val tagId: Long
)
