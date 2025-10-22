package com.example.angiday.repository

import com.example.angiday.db.FoodDao

class FoodRepository(private val dao: FoodDao) {

    fun getAllFoods() = dao.getAllFoods()

    fun searchFoods(keyword: String) = dao.searchFoods(keyword)

    fun getFoodsByAnyIngredients(ingredientNames: List<String>) =
        dao.getFoodsByAnyIngredients(ingredientNames)

    fun getFood(foodId: Long) = dao.getFood(foodId)

    suspend fun getRandomFoods(count: Int = 3) = dao.getRandomFoods(count)

    fun breakfastFoods() = dao.getFoodsByTag("Bữa sáng")
    fun lunchFoods()     = dao.getFoodsByTag("Bữa trưa")   // hoặc getFoodsByCategory("Món cơm")
    fun dinnerFoods()    = dao.getFoodsByTag("Bữa tối")

}
