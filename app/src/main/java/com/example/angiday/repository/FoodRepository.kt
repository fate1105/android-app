package com.example.angiday.repository

import com.example.angiday.db.FoodDao
import com.example.angiday.model.entity.FoodEntity
import com.example.angiday.model.entity.UserProfileEntity
import com.example.angiday.model.relations.FoodWithRelations

class FoodRepository(private val dao: FoodDao) {

    fun getAllFoods() = dao.getAllFoods()

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
    fun getFoodsByCategory(categoryName: String) =
        dao.getFoodsByCategory(categoryName)
    suspend fun getRecommendedFoods(
        profile: UserProfileEntity,
        limit: Int
    ): List<FoodWithRelations> {

        val allFoods = dao.getAllFoodsWithRelations()

        return allFoods
            .map { food ->
                val score = computeFoodScore(profile, food)
                Pair(score, food)
            }
            .filter { it.first > -100 } // loại các món dị ứng
            .sortedByDescending { it.first }
            .take(limit)
            .map { it.second }
    }

    private fun computeFoodScore(
        profile: UserProfileEntity,
        food: FoodWithRelations
    ): Int {

        var score = 0

        // ===== 1. Kiểm tra dị ứng ===== //
        val allergies = profile.allergies
            ?.split(",")
            ?.map { it.trim().lowercase() }
            ?: emptyList()

        if (allergies.isNotEmpty()) {
            val ingNames = food.ingredients.map { it.name.lowercase() }
            if (allergies.any { allergen ->
                    ingNames.any { it.contains(allergen) }
                }) {
                return -999
            }
        }

        // ===== 2. Tag cay ===== //
        val hasSpicy = food.tags.any {
            it.name.lowercase().contains("cay")
        }

        val userSpicy = profile.spicyLevel ?: 2

        if (hasSpicy) {
            score += if (userSpicy >= 3) 3 else -2
        }

        // ===== 3. Tag thịt / rau ===== //
        val categoryName = food.category?.name?.lowercase() ?: ""

        val hasMeat = categoryName.contains("mặn")
        val hasVeg  = categoryName.contains("chay")

        if (profile.preferMeat == 1 && hasMeat) score += 3
        if (profile.preferVeg == 1 && hasVeg)   score += 3

        // ===== 4. Calories ===== //
        val foodCal = food.food.calories ?: 0
        val targetCal = getTargetCalorie(profile)
        val diff = kotlin.math.abs(foodCal - targetCal)

        score -= diff / 40  // càng gần target càng tốt


        // ===== 5. Random nhỏ để đa dạng ===== //
        score += (0..2).random()

        return score
    }

    private fun calculateBMI(profile: UserProfileEntity): Double? {
        val h = profile.height ?: return null
        val w = profile.weight ?: return null
        if (h <= 0 || w <= 0) return null

        val heightM = h / 100.0
        return w / (heightM * heightM)
    }

    private fun getTargetCalorie(profile: UserProfileEntity): Int {
        val bmi = calculateBMI(profile) ?: return 450

        return when {
            bmi < 18.5 -> 600  // tăng cân
            bmi < 25   -> 450  // duy trì
            bmi < 30   -> 350  // giảm cân nhẹ
            else       -> 300  // giảm cân mạnh
        }
    }

}
