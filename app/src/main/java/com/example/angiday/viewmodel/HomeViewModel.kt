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

class HomeViewModel(
    private val metaRepo: MetaRepository,
    private val foodRepo: FoodRepository
) : ViewModel() {

    // ----- Constants -----
    companion object {
        private const val PREF_NAME = "daily_meals"
        private const val KEY_LAST_DATE = "last_date"
        private const val KEY_MEALS_JSON = "meals_json"
    }

    // ----- StateFlow -----
    val ingredients: StateFlow<List<IngredientEntity>> =
        metaRepo.getAllIngredients()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categories = metaRepo.getAllCategories()

    private val _randomMeals = MutableStateFlow<List<FoodWithRelations>>(emptyList())
    val randomMeals: StateFlow<List<FoodWithRelations>> = _randomMeals.asStateFlow()

    // ----- Public Methods -----
    fun loadRandomMeals(context: Context) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val today = getCurrentDate()
            val lastDate = prefs.getString(KEY_LAST_DATE, null)

            val cachedMeals = if (lastDate == today) loadCachedMeals(prefs) else null
            if (cachedMeals != null) {
                _randomMeals.value = cachedMeals
                return@launch
            }

            val newMeals = foodRepo.getRandomFoods(3)
            _randomMeals.value = newMeals
            saveMealsToPrefs(prefs, today, newMeals)
        }
        Log.d("SharedPrefs", "Loaded meals: ${Gson().toJson(_randomMeals.value)}")

    }

    // ----- Helper Functions -----
    private fun getCurrentDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun loadCachedMeals(prefs: android.content.SharedPreferences): List<FoodWithRelations>? {
        val json = prefs.getString(KEY_MEALS_JSON, null) ?: return null
        return runCatching {
            Gson().fromJson(json, Array<FoodWithRelations>::class.java).toList()
        }.getOrNull()
    }

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
