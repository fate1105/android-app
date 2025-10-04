package com.example.angiday.repository

import com.example.angiday.db.MetaDao
import com.example.angiday.model.entity.CategoryEntity
import com.example.angiday.model.entity.IngredientEntity
import com.example.angiday.model.entity.TagEntity
import kotlinx.coroutines.flow.Flow

class MetaRepository(private val dao: MetaDao) {

    // ===== Categories =====
    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return dao.getAllCategories()
    }

    fun getCategory(id: Long): Flow<CategoryEntity> {
        return dao.getCategory(id)
    }

    // ===== Tags =====
    fun getAllTags(): Flow<List<TagEntity>> {
        return dao.getAllTags()
    }

    fun getTag(id: Long): Flow<TagEntity> {
        return dao.getTag(id)
    }
    fun getAllIngredients(): Flow<List<IngredientEntity>> =
        dao.getAllIngredients() // bạn thêm query trong MetaDao
}
