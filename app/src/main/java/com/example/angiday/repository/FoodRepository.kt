package com.example.angiday.repository

import com.example.angiday.db.FoodDao
import com.example.angiday.model.entity.FoodEntity
import com.example.angiday.model.entity.UserProfileEntity
import com.example.angiday.model.relations.FoodWithRelations

class FoodRepository(private val dao: FoodDao) {

    // ================= BASIC QUERIES ================= //
    fun getAllFoods() = dao.getAllFoods()
    fun getFoodsByAnyIngredients(names: List<String>) = dao.getFoodsByAnyIngredients(names)
    fun getFood(id: Long) = dao.getFood(id)
    suspend fun getFoodEntityById(id: Long) = dao.getFoodById(id)
    suspend fun getRandomFoods(count: Int) = dao.getRandomFoods(count)
    suspend fun getRandomFood() = dao.getRandomFood()
    fun getFoodsByCategory(name: String) = dao.getFoodsByCategory(name)

    suspend fun getRecommendedFoods(
        profile: UserProfileEntity,
        limit: Int
    ): List<FoodWithRelations> {
        val allFoods = dao.getAllFoodsWithRelations()
        val foods = if (allFoods.size > 200)
            allFoods.shuffled().take(200)
        else
            allFoods

        // Chuẩn bị dữ liệu cache
        val allergies = profile.allergies
            ?.split(",")
            ?.map { it.trim().lowercase() }
            ?: emptyList()

        val spicyLevel = profile.spicyLevel ?: 2
        val preferMeat = profile.preferMeat == 1
        val preferVeg = profile.preferVeg == 1
        val targetCal = getTargetCalorie(profile)

        return foods
            .asSequence()
            .map { food ->
                val score = fastComputeScore(
                    food, allergies, spicyLevel, preferMeat, preferVeg, targetCal
                )
                Pair(score, food)
            }
            .filter { it.first > -500 }
            .sortedByDescending { it.first }
            .take(limit)
            .map { it.second }
            .toList()
    }

    private fun fastComputeScore(
        food: FoodWithRelations,
        allergies: List<String>,
        spicyLevel: Int,
        preferMeat: Boolean,
        preferVeg: Boolean,
        targetCal: Int
    ): Int {

        var score = 0

        if (allergies.isNotEmpty()) {
            val ing = food.ingredients.map { it.name.lowercase() }
            if (allergies.any { allergen -> ing.any { it.contains(allergen) } }) {
                return -999
            }
        }

        val hasSpicy = food.tags.any { it.name.contains("cay", ignoreCase = true) }
        if (hasSpicy) {
            score += if (spicyLevel >= 3) 3 else -2
        }

        val category = food.category?.name?.lowercase() ?: ""

        if (preferMeat && category.contains("mặn")) score += 3
        if (preferVeg  && category.contains("chay")) score += 3

        val cal = food.food.calories ?: 0
        val diff = kotlin.math.abs(cal - targetCal)
        score -= diff / 40
        score += (cal % 3)
        return score
    }
    private fun calculateBMI(profile: UserProfileEntity): Double? {
        val h = profile.height ?: return null
        val w = profile.weight ?: return null
        if (h <= 0 || w <= 0) return null
        return w / ((h / 100.0) * (h / 100.0))
    }

    private fun getTargetCalorie(profile: UserProfileEntity): Int {
        val bmi = calculateBMI(profile) ?: return 450
        return when {
            bmi < 18.5 -> 600
            bmi < 25   -> 450
            bmi < 30   -> 350
            else       -> 300
        }
    }

}
