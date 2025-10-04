package com.example.angiday.model.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.angiday.model.entity.*

data class FoodWithRelations(
    @Embedded val food: FoodEntity,

    // Ingredients
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = FoodIngredientCrossRef::class,
            parentColumn = "foodId",
            entityColumn = "ingredientId"
        )
    )
    val ingredients: List<IngredientEntity>,

    // Tags
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = FoodTagCrossRef::class,
            parentColumn = "foodId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>,

    // Category (1 món ăn chỉ thuộc 1 category)
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?
)
