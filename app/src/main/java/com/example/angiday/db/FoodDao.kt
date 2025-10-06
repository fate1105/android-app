package com.example.angiday.db

import androidx.room.*
import com.example.angiday.model.relations.FoodWithRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    // ===== Query =====
    @Transaction
    @Query("SELECT * FROM foods ORDER BY id DESC")
    fun getAllFoods(): Flow<List<FoodWithRelations>>

    @Transaction
    @Query("""
        SELECT * FROM foods 
        WHERE title LIKE '%' || :keyword || '%' 
           OR "desc"  LIKE '%' || :keyword || '%'
        ORDER BY id DESC
    """)
    fun searchFoods(keyword: String): Flow<List<FoodWithRelations>>

    @Transaction
    @Query("SELECT * FROM foods WHERE id = :foodId")
    fun getFood(foodId: Long): Flow<FoodWithRelations>

    // Lọc theo danh sách nguyên liệu: món có ít nhất 1 nguyên liệu trùng
    @Transaction
    @Query("""
        SELECT DISTINCT f.* FROM foods f
        JOIN food_ingredient_crossref j ON f.id = j.foodId
        JOIN ingredients i ON i.id = j.ingredientId
        WHERE i.name IN (:ingredientNames)
        ORDER BY f.id DESC
    """)
    fun getFoodsByAnyIngredients(ingredientNames: List<String>): Flow<List<FoodWithRelations>>


}
