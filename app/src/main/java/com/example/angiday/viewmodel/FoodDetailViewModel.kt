package com.example.angiday.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.angiday.repository.FoodRepository
import com.example.angiday.model.relations.FoodWithRelations
import kotlinx.coroutines.flow.Flow

class FoodDetailViewModel(private val repo: FoodRepository) : ViewModel() {
    fun getFood(foodId: Long): Flow<FoodWithRelations?> {
        return repo.getFood(foodId)
    }
}

class FoodDetailViewModelFactory(
    private val repo: FoodRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodDetailViewModel::class.java)) {
            return FoodDetailViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
