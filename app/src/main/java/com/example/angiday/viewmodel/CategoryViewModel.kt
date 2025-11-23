package com.example.angiday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.angiday.repository.FoodRepository
import com.example.angiday.model.relations.FoodWithRelations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val repo: FoodRepository) : ViewModel() {

    private val _foods = MutableStateFlow<List<FoodWithRelations>>(emptyList())
    val foods: StateFlow<List<FoodWithRelations>> = _foods

    fun loadCategoryFoods(category: String) {
        viewModelScope.launch {
            repo.getFoodsByCategory(category).collect { newList ->
                if (newList != _foods.value) {
                    _foods.value = newList
                }
            }
        }
    }

}
class CategoryViewModelFactory(
    private val repo: FoodRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}