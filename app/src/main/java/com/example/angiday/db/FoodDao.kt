package com.example.angiday.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.angiday.model.relations.FoodWithRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    // ===== Menu theo TAG (vd: "Bữa sáng", "Bữa trưa", "Bữa tối")
    @Transaction
    @Query("""
        SELECT f.* FROM foods f
        INNER JOIN food_tag_crossref ft ON ft.foodId = f.id
        INNER JOIN tags t ON t.id = ft.tagId
        WHERE t.name = :tagName
        ORDER BY f.id DESC
    """)
    fun getFoodsByTag(tagName: String): Flow<List<FoodWithRelations>>

    // ===== Menu theo CATEGORY (vd: "Món cơm", "Món nước" ...)
    @Transaction
    @Query("""
        SELECT f.* FROM foods f
        INNER JOIN categories c ON c.id = f.categoryId
        WHERE c.name = :categoryName
        ORDER BY f.id DESC
    """)
    fun getFoodsByCategory(categoryName: String): Flow<List<FoodWithRelations>>

    // ===== Danh sách / tìm kiếm / chi tiết
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

    // ===== Lọc theo danh sách nguyên liệu: món có ÍT NHẤT 1 nguyên liệu trùng
    @Transaction
    @Query("""
        SELECT DISTINCT f.* FROM foods f
        JOIN food_ingredient_crossref j ON f.id = j.foodId
        JOIN ingredients i ON i.id = j.ingredientId
        WHERE i.name IN (:ingredientNames)
        ORDER BY f.id DESC
    """)
    fun getFoodsByAnyIngredients(ingredientNames: List<String>): Flow<List<FoodWithRelations>>

    @Transaction
    @Query("SELECT * FROM foods ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomFoods(count: Int = 3): List<FoodWithRelations>

}
