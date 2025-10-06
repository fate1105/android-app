package com.example.angiday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.angiday.model.relations.FoodWithRelations
import com.example.angiday.repository.FoodRepository
import kotlinx.coroutines.flow.*

class MenuViewModel(private val repo: FoodRepository) : ViewModel() {

    // Lấy tất cả và group theo Category
    val groupedFoods: StateFlow<Map<String, List<FoodWithRelations>>> =
        repo.getAllFoods()
            .map { foods ->
                foods.groupBy { it.category?.name ?: "Khác" }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    // Nếu cần lấy riêng từng món
    fun getFoodById(id: Long): Flow<FoodWithRelations> = repo.getFood(id)
}
