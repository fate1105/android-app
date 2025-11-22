package com.example.angiday.repository

import android.content.SharedPreferences
import com.example.angiday.db.MetaDao
import com.example.angiday.db.dao.UserProfileDao
import com.example.angiday.model.entity.CategoryEntity
import com.example.angiday.model.entity.IngredientEntity
import com.example.angiday.model.entity.TagEntity
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

    fun getCategory(id: Long): Flow<CategoryEntity> =
        metaDao.getCategory(id)

    // ===== Tags =====
    fun getAllTags(): Flow<List<TagEntity>> =
        metaDao.getAllTags()

    fun getTag(id: Long): Flow<TagEntity> =
        metaDao.getTag(id)

    // ===== Ingredients =====
    fun getAllIngredients(): Flow<List<IngredientEntity>> =
        metaDao.getAllIngredients()

    // ===== User Profile =====
    suspend fun getUserProfile(): UserProfileEntity? {
        val userId = session.getUserId()
        if (userId <= 0) return null
        return userProfileDao.getByUserId(userId)
    }

    suspend fun deleteUserProfile(userId: Long) {
        userProfileDao.deleteByUserId(userId)
    }
}
