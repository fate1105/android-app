package com.example.angiday.repository

import com.example.angiday.db.FoodDao

class FoodRepository(private val dao: FoodDao) {

    fun getAllFoods() = dao.getAllFoods()

    fun searchFoods(keyword: String) = dao.searchFoods(keyword)

    fun getFoodsByAnyIngredients(ingredientNames: List<String>) =
        dao.getFoodsByAnyIngredients(ingredientNames)

    fun getFood(foodId: Long) = dao.getFood(foodId)

}
