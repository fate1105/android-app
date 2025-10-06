// com/example/angiday/session/SessionManager.kt
package com.example.angiday.session

import android.content.Context
import android.content.SharedPreferences
import com.example.angiday.model.entity.UserEntity

class SessionManager(context: Context) {
    private val sp: SharedPreferences =
        context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: UserEntity) {
        sp.edit()
            .putLong("user_id", user.id)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .apply()
    }

    fun isLoggedIn(): Boolean = sp.getLong("user_id", -1L) > 0

    fun clear() { sp.edit().clear().apply() }
}
