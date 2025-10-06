package com.example.angiday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.angiday.model.entity.IngredientEntity
import com.example.angiday.model.relations.FoodWithRelations
import com.example.angiday.repository.FoodRepository
import com.example.angiday.repository.MetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(    private val metaRepo: MetaRepository,
                        private val foodRepo: FoodRepository
) : ViewModel() {

    val ingredients: StateFlow<List<IngredientEntity>> =
        metaRepo.getAllIngredients()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    private val _randomMeals = MutableStateFlow<List<FoodWithRelations>>(emptyList())
    val randomMeals: StateFlow<List<FoodWithRelations>> = _randomMeals

    fun loadRandomMeals() {
        viewModelScope.launch {
            _randomMeals.value = foodRepo.getRandomFoods(3)
        }
    }
    val categories = metaRepo.getAllCategories()
}
class HomeViewModelFactory(
    private val metaRepo: MetaRepository,
    private val foodRepo: FoodRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(metaRepo, foodRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
