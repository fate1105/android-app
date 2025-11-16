package com.example.angiday.db.dao

import androidx.room.*
import com.example.angiday.model.entity.UserEntity

@Dao

interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?


    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("""
        SELECT * FROM users 
        WHERE email = :email AND password = :password 
        LIMIT 1
    """)
    suspend fun findByEmailAndPassword(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserEntity>

    @Update
    suspend fun update(user: UserEntity)
}
