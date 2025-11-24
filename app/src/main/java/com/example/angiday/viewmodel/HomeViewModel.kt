package com.example.angiday.viewmodel

import android.content.Context
import android.content.SharedPreferences
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
    private var isLoaded = false
    companion object {
        private const val PREF_NAME = "daily_home"

        //Tách cache thành 2 key riêng
        private const val KEY_LAST_DATE_MEALS = "last_date_meals"
        private const val KEY_LAST_DATE_FEATURED = "last_date_featured"

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

    // ============ DAILY 3 MEALS ============= //
    private val _randomMeals = MutableStateFlow<List<FoodWithRelations>>(emptyList())
    val randomMeals: StateFlow<List<FoodWithRelations>> = _randomMeals.asStateFlow()

    // ============ FEATURED FOOD ============= //
    private val _featuredFood = MutableStateFlow<FoodEntity?>(null)
    val featuredFood = _featuredFood.asStateFlow()

    private val _featuredFoodId = MutableStateFlow<Long?>(null)
    val featuredFoodId = _featuredFoodId.asStateFlow()

  fun loadHomeData(context: Context) {
        if (isLoaded) return   // 🔥 CHẶN xử lý lặp
        isLoaded = true

        viewModelScope.launch {
            loadDailyMeals(context)
            loadFeaturedFood(context)
        }
    }

     private suspend fun loadDailyMeals(context: Context) {
        val pref = prefs(context)
        val today = getToday()
        val lastDate = pref.getString(KEY_LAST_DATE_MEALS, "")

        // ➤ Dùng cache trong ngày nếu có
        if (today == lastDate) {
            loadCachedMeals(pref)?.let {
                _randomMeals.value = it
                return
            }
        }

        //  Loading mới bằng recommender
        val profile = metaRepo.getUserProfile()

        val meals = if (profile != null)
            foodRepo.getRecommendedFoods(profile, 3)
        else
            foodRepo.getRandomFoods(3)

        _randomMeals.value = meals

        // Lưu cache
        pref.edit()
            .putString(KEY_LAST_DATE_MEALS, today)
            .putString(KEY_MEALS_JSON, Gson().toJson(meals))
            .apply()
    }

    private fun loadCachedMeals(pref: SharedPreferences): List<FoodWithRelations>? {
        val json = pref.getString(KEY_MEALS_JSON, null) ?: return null
        return runCatching {
            Gson().fromJson(json, Array<FoodWithRelations>::class.java).toList()
        }.getOrNull()
    }

    private suspend fun loadFeaturedFood(context: Context) {
        val pref = prefs(context)
        val today = getToday()
        val lastDate = pref.getString(KEY_LAST_DATE_FEATURED, "")

        // Nếu đã cache trong ngày → lấy featured id
        val savedId =
            if (today == lastDate) pref.getLong(KEY_FEATURED_ID, -1L).takeIf { it > 0 }
            else null

        if (savedId != null) {
            val food = foodRepo.getFoodEntityById(savedId)
            _featuredFood.value = food
            _featuredFoodId.value = savedId
            return
        }

        // FEATURED PERSONALIZED
        val profile = metaRepo.getUserProfile()

        val selected = if (profile != null) {
            foodRepo.getRecommendedFoods(profile, 1).firstOrNull()
        } else {
            foodRepo.getRandomFood()
        }

        val food = selected?.food ?: return

        // Lưu cache featured
        pref.edit()
            .putString(KEY_LAST_DATE_FEATURED, today)
            .putLong(KEY_FEATURED_ID, food.id)
            .apply()

        _featuredFood.value = food
        _featuredFoodId.value = food.id
    }

  private fun getToday(): String =
        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
}

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
