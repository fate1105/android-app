package com.example.angiday.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    // Cho List<String> → allergies
    @TypeConverter
    fun fromAllergies(list: List<String>?): String? {
        return list?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toAllergies(json: String?): List<String> {
        return if (json.isNullOrBlank()) emptyList()
        else Gson().fromJson(json, object : TypeToken<List<String>>() {}.type)
    }

    // Cho Boolean → INTEGER (0/1)
    @TypeConverter
    fun fromBoolean(value: Boolean): Int = if (value) 1 else 0

    @TypeConverter
    fun toBoolean(value: Int): Boolean = value == 1
}