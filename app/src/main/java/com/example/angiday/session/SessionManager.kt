package com.example.angiday.session

import android.content.Context
import android.content.SharedPreferences
import com.example.angiday.model.entity.UserEntity

class SessionManager(context: Context) {
    private val sp: SharedPreferences =
        context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    // 🔹 Lưu thông tin user sau khi đăng nhập hoặc đăng ký
    fun saveUser(user: UserEntity) {
        sp.edit()
            .putLong("user_id", user.id)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .apply()
    }

    // 🔹 Lấy ID người dùng hiện tại
    fun getUserId(): Long = sp.getLong("user_id", -1L)

    // 🔹 Lấy tên người dùng
    fun getUserName(): String? = sp.getString("user_name", null)

    // 🔹 Lấy email người dùng
    fun getUserEmail(): String? = sp.getString("user_email", null)

    // 🔹 Kiểm tra trạng thái đăng nhập
    fun isLoggedIn(): Boolean = getUserId() > 0
    fun isRemembered(): Boolean = sp.getBoolean("remember_login", false)
    // 🔥 Lưu user + trạng thái Remember Login
    fun saveUser(user: UserEntity, remember: Boolean) {
        sp.edit()
            .putLong("user_id", user.id)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .putBoolean("remember_login", remember)
            .apply()
    }
    // 👉 Gọi khi mới đăng ký (chưa có UserEntity đầy đủ)
    fun saveUserId(userId: Long, email: String) {
        sp.edit()
            .putLong("user_id", userId)
            .putString("user_email", email)
            .putBoolean("remember_login", true)  // auto remember
            .apply()
    }

    fun clear() {
        sp.edit().clear().apply()
    }
    fun clearUserButKeepRemember() {
        sp.edit()
            .remove("user_id")
            .remove("user_name")
            .remove("user_email")
            .apply()
    }
}
