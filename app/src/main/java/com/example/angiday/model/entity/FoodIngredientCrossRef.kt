package com.example.angiday.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "food_ingredient_crossref",
    primaryKeys = ["foodId", "ingredientId"],
    foreignKeys = [
        ForeignKey(
            entity = FoodEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FoodIngredientCrossRef(
    val foodId: Long,
    val ingredientId: Long
)
