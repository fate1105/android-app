package com.example.angiday.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.angiday.model.entity.FoodEntity
import com.example.angiday.model.entity.UserEntity

data class FoodWithUser(
    @Embedded val food: FoodEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val user: UserEntity? = null
)
