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

    @Query("SELECT * FROM user_behavior WHERE userId = :userId AND foodId = :foodId LIMIT 1")
    suspend fun getBehavior(userId: Long, foodId: Long): UserBehaviorEntity?

    @Query("DELETE FROM user_behavior WHERE userId = :userId AND foodId = :foodId AND behaviorType = 'favorite'")
    suspend fun delete(userId: Int, foodId: Int)

    @Query("""
    SELECT f.* FROM foods f
    INNER JOIN user_behavior ub ON ub.foodId = f.id
    WHERE ub.userId = :userId AND ub.behaviorType = 'cooked'
""")
    suspend fun getCookedFoodsWithDetail(userId: Int): List<FoodWithRelations>
    // ❌ Xóa bài chia sẻ của người dùng
    @Query("DELETE FROM user_behavior WHERE userId = :userId AND foodId = :foodId AND behaviorType = :type")
    suspend fun deleteBehavior(userId: Long, foodId: Long, type: String)



    // 🧡 Lấy món đã chia sẻ
    @Query("""
        SELECT f.* FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'shared'
    """)
    suspend fun getSharedFoodsWithDetail(userId: Int): List<FoodWithRelations>

    // ❤️ Lấy món yêu thích
    @Query("""
        SELECT f.* FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'favorite'
    """)
    suspend fun getFavoriteFoodsWithDetail(userId: Int): List<FoodWithRelations>
}
