package com.example.angiday.db

import androidx.room.Dao
import androidx.room.Query
import com.example.angiday.model.entity.CategoryEntity
import com.example.angiday.model.entity.IngredientEntity
import com.example.angiday.model.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaDao {

    // ===== Categories =====
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    fun getCategory(id: Long): Flow<CategoryEntity>


    // ===== Tags =====
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE id = :id LIMIT 1")
    fun getTag(id: Long): Flow<TagEntity>
    // ===== Ingredient =====
    @Query("SELECT * FROM ingredients ORDER BY name ASC")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

}
