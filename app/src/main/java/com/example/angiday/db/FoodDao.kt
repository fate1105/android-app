package com.example.angiday.db

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.angiday.model.entity.FoodEntity
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
    @Query("""
        SELECT DATE(timestamp) AS day
        FROM user_behavior
        WHERE userId = :userId AND behaviorType = 'cooked'
        GROUP BY DATE(timestamp)
        ORDER BY DATE(timestamp) DESC
    """)
    suspend fun getCookedDays(userId: Int): List<String>
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
    @Insert
    suspend fun insert(food: FoodEntity): Long
    @Query("SELECT * FROM foods WHERE id = :id LIMIT 1")
    suspend fun getFoodById(id: Long): FoodEntity?

    @Transaction
    @Query("SELECT * FROM foods WHERE id = :foodId")
    fun getFood(foodId: Long): Flow<FoodWithRelations>

    @Query("""
    SELECT f.* 
    FROM foods f
    JOIN user_behavior ub ON ub.foodId = f.id
    WHERE ub.userId = :userId
      AND ub.behaviorType = 'cooked'
      AND DATE(ub.timestamp / 1000, 'unixepoch') = :date
""")
    suspend fun getFoodsCookedOnDate(userId: Long, date: String): List<FoodEntity>

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
    @Query("SELECT * FROM foods")
    fun getAllCursor(): Cursor

    @Query("SELECT * FROM foods WHERE id = :id")
    fun getByIdCursor(id: Long): Cursor
}
