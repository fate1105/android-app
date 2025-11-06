package com.example.angiday.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.angiday.model.entity.FoodEntity
import com.example.angiday.model.entity.UserBehaviorEntity
import com.example.angiday.model.relations.FoodWithRelations

@Dao
interface UserBehaviorDao {

    // 🟢 Ghi nhận hành vi người dùng (yêu thích, chia sẻ, nấu, v.v.)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(behavior: UserBehaviorEntity)

    // 🔍 Kiểm tra hành vi của người dùng với món ăn cụ thể
    @Query("SELECT * FROM user_behavior WHERE userId = :userId AND foodId = :foodId LIMIT 1")
    suspend fun getBehavior(userId: Long, foodId: Long): UserBehaviorEntity?

    // ❌ Xóa hành vi yêu thích (favorite)
    @Query("DELETE FROM user_behavior WHERE userId = :userId AND foodId = :foodId AND behaviorType = 'favorite'")
    suspend fun deleteFavorite(userId: Int, foodId: Long)

    // ❌ Xóa hành vi bất kỳ (shared, cooked, favorite)
    @Query("DELETE FROM user_behavior WHERE userId = :userId AND foodId = :foodId AND behaviorType = :type")
    suspend fun deleteBehavior(userId: Long, foodId: Long, type: String)
    // ❌ Xóa riêng hành vi yêu thích (favorite)
    @Query("DELETE FROM user_behavior WHERE userId = :userId AND foodId = :foodId AND behaviorType = 'favorite'")
    suspend fun delete(userId: Int, foodId: Int)
    // 🍳 Danh sách món đã nấu
    @Query("""
        SELECT f.* FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'cooked'
    """)
    suspend fun getCookedFoodsWithDetail(userId: Int): List<FoodWithRelations>

    // 📤 Danh sách món đã chia sẻ của người dùng hiện tại
    @Query("""
        SELECT f.* FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'shared'
        ORDER BY ub.id DESC
    """)
    suspend fun getSharedFoodsWithDetail(userId: Int): List<FoodWithRelations>

    // ❤️ Danh sách món yêu thích
    @Query("""
        SELECT f.* FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'favorite'
    """)
    suspend fun getFavoriteFoodsWithDetail(userId: Int): List<FoodWithRelations>

    // 🌐 CỘNG ĐỒNG – Tất cả món được chia sẻ bởi mọi người
    @Query("""
        SELECT f.* FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.behaviorType = 'shared'
        ORDER BY ub.id DESC
    """)
    suspend fun getAllSharedFoods(): List<FoodEntity>

    @Query("SELECT * FROM user_behavior WHERE behaviorType = 'shared'")
    suspend fun getAllSharedBehavior(): List<UserBehaviorEntity>



}
