package com.example.angiday.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.angiday.model.entity.IngredientEntity
import com.example.angiday.model.entity.FoodEntity
import com.example.angiday.model.relations.FoodWithRelations
import com.example.angiday.repository.FoodRepository
import com.example.angiday.repository.MetaRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(
    private val metaRepo: MetaRepository,
    private val foodRepo: FoodRepository
) : ViewModel() {

    companion object {
        private const val PREF_NAME = "daily_home"
        private const val KEY_LAST_DATE = "last_date"
        private const val KEY_MEALS_JSON = "meals_json"
        private const val KEY_FEATURED_ID = "featured_food_id"
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ============ INGREDIENTS ============= //
    val ingredients: StateFlow<List<IngredientEntity>> =
        metaRepo.getAllIngredients()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ============ CATEGORIES ============= //
    val categories = metaRepo.getAllCategories()

    // ============ RANDOM 3 MEALS ============= //
    private val _randomMeals = MutableStateFlow<List<FoodWithRelations>>(emptyList())
    val randomMeals: StateFlow<List<FoodWithRelations>> = _randomMeals.asStateFlow()

    // ============ FEATURED FOOD (món nổi bật) ============= //
    private val _featuredFood = MutableStateFlow<FoodEntity?>(null)
    val featuredFood = _featuredFood.asStateFlow()
    private val _featuredFoodId = MutableStateFlow<Long?>(null)
    val featuredFoodId = _featuredFoodId.asStateFlow()


    // ====================================================================== //
    //  LOAD TẤT CẢ DỮ LIỆU HOME (gọi 1 lần trong HomeFragment)
    // ====================================================================== //
    fun loadHomeData(context: Context) {
        viewModelScope.launch {
            loadDailyMeals(context)
            loadFeaturedFood(context)
        }
    }

    // ====================================================================== //
    //  1) Load 3 món ngẫu nhiên mỗi ngày
    // ====================================================================== //
    private suspend fun loadDailyMeals(context: Context) {
        val pref = prefs(context)
        val today = getToday()
        val lastDate = pref.getString(KEY_LAST_DATE, "")

        // Nếu đã cache trong ngày
        if (today == lastDate) {
            val cached = loadCachedMeals(pref)
            if (cached != null) {
                _randomMeals.value = cached
                return
            }
        }

        // Lấy ngẫu nhiên mới → lưu cache
        val meals = foodRepo.getRandomFoods(3)
        _randomMeals.value = meals
        saveMeals(pref, today, meals)
    }

    private fun loadCachedMeals(pref: SharedPreferences): List<FoodWithRelations>? {
        val json = pref.getString(KEY_MEALS_JSON, null) ?: return null
        return runCatching {
            Gson().fromJson(json, Array<FoodWithRelations>::class.java).toList()
        }.getOrNull()
    }

    private fun saveMeals(pref: SharedPreferences, date: String, meals: List<FoodWithRelations>) {
        pref.edit()
            .putString(KEY_LAST_DATE, date)
            .putString(KEY_MEALS_JSON, Gson().toJson(meals))
            .apply()
    }

    // ====================================================================== //
    //  2) Load Featured Food (món nổi bật – 1 lần / ngày)
    // ====================================================================== //
    private suspend fun loadFeaturedFood(context: Context) {
        val pref = prefs(context)
        val today = getToday()
        val lastDate = pref.getString(KEY_LAST_DATE, "")

        val savedId =
            if (today == lastDate) pref.getLong(KEY_FEATURED_ID, -1L).takeIf { it > 0 }
            else null

        // Nếu đã có trong cache
        if (savedId != null) {
            val food = foodRepo.getFoodEntityById(savedId)
            _featuredFood.value = food
            _featuredFoodId.value = savedId
            return
        }

        // Random mới
        val random = foodRepo.getRandomFood()
        val food = random?.food ?: return

        // Save ID
        pref.edit().putLong(KEY_FEATURED_ID, food.id).apply()

        _featuredFood.value = food
        _featuredFoodId.value = food.id
    }

    // ====================================================================== //
    private fun getToday(): String =
        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
}

// ================= FACTORY ================= //

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
