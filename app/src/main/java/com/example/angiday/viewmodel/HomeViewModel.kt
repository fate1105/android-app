package com.example.angiday.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.angiday.model.entity.IngredientEntity
import com.example.angiday.model.relations.FoodWithRelations
import com.example.angiday.repository.FoodRepository
import com.example.angiday.repository.MetaRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * HomeViewModel
 * Quản lý logic hiển thị cho HomeFragment:
 * - Load danh sách nguyên liệu và danh mục món ăn
 * - Gợi ý ngẫu nhiên 3 bữa ăn/ngày (cache bằng SharedPreferences)
 */
class HomeViewModel(
    private val metaRepo: MetaRepository,
    private val foodRepo: FoodRepository
) : ViewModel() {

    companion object {
        private const val PREF_NAME = "daily_meals"
        private const val KEY_LAST_DATE = "last_date"
        private const val KEY_MEALS_JSON = "meals_json"
    }

    // Dòng dữ liệu nguyên liệu (Ingredient)
    val ingredients: StateFlow<List<IngredientEntity>> =
        metaRepo.getAllIngredients()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Dòng dữ liệu danh mục món ăn
    val categories = metaRepo.getAllCategories()

    // Dòng dữ liệu 3 bữa ăn ngẫu nhiên
    private val _randomMeals = MutableStateFlow<List<FoodWithRelations>>(emptyList())
    val randomMeals: StateFlow<List<FoodWithRelations>> = _randomMeals.asStateFlow()

    /**
     * Load 3 món ngẫu nhiên (cache mỗi ngày)
     * Nếu hôm nay đã có dữ liệu trong SharedPreferences → load lại từ cache.
     */
    fun loadRandomMeals(context: Context) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val today = getCurrentDate()
            val lastDate = prefs.getString(KEY_LAST_DATE, null)

            // Nếu đã cache trong ngày → đọc lại
            val cachedMeals = if (lastDate == today) loadCachedMeals(prefs) else null
            if (cachedMeals != null) {
                _randomMeals.value = cachedMeals
                return@launch
            }

            // Nếu chưa có cache → lấy ngẫu nhiên và lưu lại
            val newMeals = foodRepo.getRandomFoods(3)
            _randomMeals.value = newMeals
            saveMealsToPrefs(prefs, today, newMeals)
        }

        Log.d("HomeViewModel", "Loaded meals: ${Gson().toJson(_randomMeals.value)}")
    }

    // Lấy ngày hiện tại dạng yyyy-MM-dd
    private fun getCurrentDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Đọc dữ liệu cache từ SharedPreferences
    private fun loadCachedMeals(prefs: android.content.SharedPreferences): List<FoodWithRelations>? {
        val json = prefs.getString(KEY_MEALS_JSON, null) ?: return null
        return runCatching {
            Gson().fromJson(json, Array<FoodWithRelations>::class.java).toList()
        }.getOrNull()
    }

    // Lưu dữ liệu cache vào SharedPreferences
    private fun saveMealsToPrefs(
        prefs: android.content.SharedPreferences,
        date: String,
        meals: List<FoodWithRelations>
    ) {
        prefs.edit()
            .putString(KEY_LAST_DATE, date)
            .putString(KEY_MEALS_JSON, Gson().toJson(meals))
            .apply()
    }
}

/**
 * Factory tạo HomeViewModel
 */
class HomeViewModelFactory(
    private val metaRepo: MetaRepository,
    private val foodRepo: FoodRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(metaRepo, foodRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
