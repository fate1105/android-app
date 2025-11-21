package com.example.angiday.repository

import com.example.angiday.db.FoodDao
import com.example.angiday.model.entity.FoodEntity

class FoodRepository(private val dao: FoodDao) {

    fun getAllFoods() = dao.getAllFoods()

    fun searchFoods(keyword: String) = dao.searchFoods(keyword)

    fun getFoodsByAnyIngredients(ingredientNames: List<String>) =
        dao.getFoodsByAnyIngredients(ingredientNames)

    fun getFood(foodId: Long) = dao.getFood(foodId)
    suspend fun getFoodEntityById(id: Long): FoodEntity? =
        dao.getFoodById(id)


    // Random nhiều món (3 bữa)
    suspend fun getRandomFoods(count: Int = 3) =
        dao.getRandomFoods(count)

    // ⭐ Random 1 món (ảnh Featured + Notification)
    suspend fun getRandomFood() =
        dao.getRandomFood()

    // Tag theo bữa ăn
    fun breakfastFoods() = dao.getFoodsByTag("Bữa sáng")
    fun lunchFoods()     = dao.getFoodsByTag("Bữa trưa")
    fun dinnerFoods()    = dao.getFoodsByTag("Bữa tối")
}
