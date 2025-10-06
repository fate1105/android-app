package com.example.angiday.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.angiday.repository.FoodRepository
import com.example.angiday.viewmodel.MenuViewModel

class MenuViewModelFactory(private val repo: FoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
