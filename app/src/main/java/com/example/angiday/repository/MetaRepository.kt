package com.example.angiday.repository

import com.example.angiday.db.MetaDao
import com.example.angiday.db.dao.UserProfileDao
import com.example.angiday.model.entity.CategoryEntity
import com.example.angiday.model.entity.IngredientEntity
import com.example.angiday.model.entity.UserProfileEntity
import com.example.angiday.session.SessionManager
import kotlinx.coroutines.flow.Flow

class MetaRepository(
    private val metaDao: MetaDao,
    private val userProfileDao: UserProfileDao,
    private val session: SessionManager
) {

    // ===== Categories =====
    fun getAllCategories(): Flow<List<CategoryEntity>> =
        metaDao.getAllCategories()

    // ===== Ingredients =====
    fun getAllIngredients(): Flow<List<IngredientEntity>> =
        metaDao.getAllIngredients()

    // ===== User Profile =====
    suspend fun getUserProfile(): UserProfileEntity? {
        val userId = session.getUserId()
        if (userId <= 0) return null
        return userProfileDao.getByUserId(userId)
    }
}
