package com.example.angiday.session

import android.content.Context
import android.content.SharedPreferences
import com.example.angiday.model.entity.UserEntity

class SessionManager(context: Context) {

    private val sp: SharedPreferences =
        context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: UserEntity, remember: Boolean) {
        sp.edit()
            .putLong("user_id", user.id)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .putBoolean("remember_login", remember)
            .apply()
    }

    fun getUserId(): Long = sp.getLong("user_id", -1L)
    fun getUserName(): String? = sp.getString("user_name", null)
    fun getUserEmail(): String? = sp.getString("user_email", null)

    fun isRemembered(): Boolean = sp.getBoolean("remember_login", false)

    fun clear() {
        sp.edit().clear().apply()
    }

    // ❗ KHÔNG được xóa user_id → sẽ làm mất user khi quay lại Profile
    fun clearUserButKeepRemember() {
        sp.edit()
            .remove("user_name")
            .remove("user_email")
            .putBoolean("remember_login", true)
            .apply()
    }
}
