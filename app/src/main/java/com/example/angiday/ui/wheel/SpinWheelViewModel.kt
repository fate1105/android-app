package com.example.angiday.ui.wheel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.relations.FoodWithRelations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpinWheelViewModel(private val db: AppDatabase) : ViewModel() {

    private val _randomFoods = MutableStateFlow<List<FoodWithRelations>>(emptyList())
    val randomFoods: StateFlow<List<FoodWithRelations>> = _randomFoods

    fun loadRandomFoods(count: Int = 7) {
        viewModelScope.launch {
            val data = db.foodDao().getRandomFoods(count)
            _randomFoods.value = data
        }
    }
    private val _favoriteFoods = MutableStateFlow<List<FoodWithRelations>>(emptyList())
    val favoriteFoods: StateFlow<List<FoodWithRelations>> = _favoriteFoods

    fun loadFavoriteFoods(userId: Int) {
        viewModelScope.launch {
            val data = db.userBehaviorDao().getFavoriteFoodsWithDetail(userId)
            _favoriteFoods.value = data
        }
    }
}

class SpinWheelViewModelFactory(
    private val db: AppDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpinWheelViewModel::class.java)) {
            return SpinWheelViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}