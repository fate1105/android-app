
package com.example.angiday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.angiday.model.relations.FoodWithRelations
import com.example.angiday.repository.FoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SuggestViewModel(private val repo: FoodRepository) : ViewModel() {

    // toàn bộ món (bạn có thể đổi sang lọc theo ingredients)
    val foods: StateFlow<List<FoodWithRelations>> =
        repo.getAllFoods()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // trong SuggestViewModel:
    fun foodsBy(ingredientNames: List<String>) =
        repo.getFoodsByAnyIngredients(ingredientNames)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

}

class SuggestViewModelFactory(private val repo: FoodRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuggestViewModel::class.java)) {
            return SuggestViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

