package com.example.angiday.utils

import android.content.Context

class ShoppingPrefs(context: Context) {

    private val prefs = context.getSharedPreferences("shopping_list", Context.MODE_PRIVATE)

    fun getItems(): MutableList<String> {
        val set = prefs.getStringSet("items", emptySet()) ?: emptySet()
        return set.toMutableList()
    }

    fun addItem(item: String) {
        val items = getItems()
        if (!items.contains(item)) {
            items.add(item)
            prefs.edit().putStringSet("items", items.toSet()).apply()
        }
    }

    fun removeItem(item: String) {
        val items = getItems()
        items.remove(item)
        prefs.edit().putStringSet("items", items.toSet()).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
