package com.example.angiday.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.angiday.model.entity.UserBehaviorEntity
import com.example.angiday.model.relations.FoodWithRelations

@Dao
interface UserBehaviorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(behavior: UserBehaviorEntity)

    @Query("""
        SELECT * 
        FROM user_behavior 
        WHERE userId = :userId AND foodId = :foodId 
        LIMIT 1
    """)
    suspend fun getBehavior(userId: Long, foodId: Long): UserBehaviorEntity?

    @Query("""
        DELETE FROM user_behavior 
        WHERE userId = :userId AND foodId = :foodId 
        AND behaviorType = 'favorite'
    """)
    suspend fun delete(userId: Int, foodId: Int)

    @Query("""
        SELECT f.* 
        FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'cooked'
    """)
    suspend fun getCookedFoodsWithDetail(userId: Int): List<FoodWithRelations>

    @Query("""
        SELECT f.* 
        FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'shared'
    """)
    suspend fun getSharedFoodsWithDetail(userId: Int): List<FoodWithRelations>

    @Query("""
        SELECT f.* 
        FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'favorite'
    """)
    suspend fun getFavoriteFoodsWithDetail(userId: Int): List<FoodWithRelations>

    @Query("""
        SELECT * 
        FROM user_behavior 
        WHERE userId = :userId AND foodId = :foodId 
        AND behaviorType = :type 
        LIMIT 1
    """)
    suspend fun getBehavior(userId: Long, foodId: Long, type: String): UserBehaviorEntity?

    @Query("""
        SELECT COUNT(*) 
        FROM user_behavior 
        WHERE userId = :userId AND foodId = :foodId 
        AND behaviorType = :type
    """)
    suspend fun exists(userId: Int, foodId: Int, type: String): Int

    @Query("""
        DELETE FROM user_behavior 
        WHERE userId = :userId AND foodId = :foodId 
        AND behaviorType = :type
    """)
    suspend fun deleteByType(userId: Int, foodId: Int, type: String)
}
