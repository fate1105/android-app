package com.example.angiday.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.angiday.model.entity.UserProfileEntity

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getByUserId(userId: Long): UserProfileEntity?

    @Query("DELETE FROM user_profiles WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)
}