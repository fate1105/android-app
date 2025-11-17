package com.example.angiday.repository

import android.content.Context
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class UserRepository private constructor(context: Context) {

    private val userDao = AppDatabase.get(context).userDao()

    companion object {
        @Volatile private var INSTANCE: UserRepository? = null

        fun get(context: Context): UserRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(context.applicationContext).also { INSTANCE = it }
            }
    }

    // ======== REGISTER – TRẢ VỀ USER ID ========
    suspend fun registerUser(
        name: String,
        email: String,
        passwordPlain: String,
        preferences: String?,
        hashPassword: Boolean
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            // Check email đã tồn tại
            val exists = userDao.findByEmail(email)
            if (exists != null) {
                return@withContext Result.failure(Exception("Email đã tồn tại"))
            }

            // Hash password
            val finalPassword = if (hashPassword) hash(passwordPlain) else passwordPlain

            // Tạo user mới
            val user = UserEntity(
                name = name,
                email = email,
                password = finalPassword,
                preferences = preferences
            )

            // INSERT 1 lần — lấy ID
            val userId = userDao.insert(user)

            // RETURN KẾT QUẢ RA NGOÀI
            return@withContext Result.success(userId)

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.failure(e)
        }
    }


    // ===================== LOGIN =====================
    suspend fun loginUser(
        email: String,
        passwordPlain: String,
        hashPassword: Boolean
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.findByEmail(email)
                ?: return@withContext Result.failure(Exception("Không tìm thấy tài khoản"))

            val inputPass = if (hashPassword) hash(passwordPlain) else passwordPlain

            return@withContext if (user.password == inputPass)
                Result.success(user)
            else
                Result.failure(Exception("Sai mật khẩu"))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ===================== UTIL =====================
    private fun hash(str: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(str.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}