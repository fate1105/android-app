// app/src/main/java/com/example/angiday/repository/UserRepository.kt
package com.example.angiday.repository

import android.content.Context
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.UserEntity
import java.security.MessageDigest

class UserRepository private constructor(context: Context) {
    private val userDao = AppDatabase.get(context).userDao()

    /** Đăng ký user mới */
    suspend fun registerUser(
        name: String,
        email: String,
        passwordPlain: String,
        preferences: String? = null,
        hashPassword: Boolean = false
    ): Result<Long> {
        // Validate cơ bản
        if (name.isBlank()) return Result.failure(IllegalArgumentException("Tên không được để trống"))
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email không được để trống"))
        if (passwordPlain.length < 6) return Result.failure(IllegalArgumentException("Mật khẩu phải ≥ 6 ký tự"))

        // Kiểm tra trùng email
        val existed = userDao.getUserByEmail(email.trim())
        if (existed != null) return Result.failure(IllegalStateException("Email đã tồn tại"))

        val passwordToSave = if (hashPassword) sha256(passwordPlain) else passwordPlain
        val entity = UserEntity(
            name = name.trim(),
            email = email.trim(),
            password = passwordToSave,
            preferences = preferences
        )

        // @Insert của bạn hiện trả về Unit → chèn rồi đọc lại để lấy id
        userDao.insertUser(entity)
        val saved = userDao.getUserByEmail(email.trim())
            ?: return Result.failure(IllegalStateException("Lưu không thành công"))
        return Result.success(saved.id)
    }

    /** Đăng nhập */
    suspend fun login(
        email: String,
        passwordPlain: String,
        hashPassword: Boolean = false
    ): Result<UserEntity> {
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email không được để trống"))
        if (passwordPlain.isBlank()) return Result.failure(IllegalArgumentException("Mật khẩu không được để trống"))

        val user = userDao.getUserByEmail(email.trim())
            ?: return Result.failure(IllegalStateException("Tài khoản không tồn tại"))

        val input = if (hashPassword) sha256(passwordPlain) else passwordPlain
        return if (user.password == input) Result.success(user)
        else Result.failure(IllegalArgumentException("Mật khẩu không đúng"))
    }

    private fun sha256(s: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(s.toByteArray())
            .joinToString("") { "%02x".format(it) }

    companion object {
        @Volatile private var INSTANCE: UserRepository? = null
        fun get(context: Context): UserRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(context.applicationContext).also { INSTANCE = it }
            }
    }
}
