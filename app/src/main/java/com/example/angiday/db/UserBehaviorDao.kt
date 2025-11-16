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

    // 🟢 Thêm hoặc cập nhật hành vi
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(behavior: UserBehaviorEntity)


    @Query("""
        SELECT * FROM user_behavior 
        WHERE userId = :userId AND foodId = :foodId 
        LIMIT 1
    """)
    suspend fun getBehavior(userId: Long, foodId: Long): UserBehaviorEntity?

    // 🔍 Lấy hành vi cụ thể theo loại (favorite, shared, cooked)
    @Query("""
        SELECT * FROM user_behavior 
        WHERE userId = :userId AND foodId = :foodId 
        AND behaviorType = :type 
        LIMIT 1
    """)
    suspend fun getBehaviorByType(userId: Long, foodId: Long, type: String): UserBehaviorEntity?

    // ❤️ Danh sách món yêu thích
    @Query("""
        SELECT f.* 
        FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'favorite'
    """)
    suspend fun getFavoriteFoodsWithDetail(userId: Int): List<FoodWithRelations>

    // 🍳 Danh sách món đã nấu
    @Query("""
        SELECT f.* 
        FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'cooked'
    """)
    suspend fun getCookedFoodsWithDetail(userId: Int): List<FoodWithRelations>

    // 📤 Danh sách món đã chia sẻ của người dùng
    @Query("""
        SELECT f.* 
        FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.userId = :userId AND ub.behaviorType = 'shared'
        ORDER BY ub.id DESC
    """)
    suspend fun getSharedFoodsWithDetail(userId: Int): List<FoodWithRelations>

    // 🌐 Tất cả món được chia sẻ trong cộng đồng
    @Query("""
        SELECT f.* FROM foods f
        INNER JOIN user_behavior ub ON ub.foodId = f.id
        WHERE ub.behaviorType = 'shared'
        ORDER BY ub.id DESC
    """)
    suspend fun getAllSharedFoods(): List<FoodEntity>

    // 🗑️ Xóa hành vi theo loại
    @Query("""
        DELETE FROM user_behavior 
        WHERE userId = :userId AND foodId = :foodId 
        AND behaviorType = :type
    """)
    suspend fun deleteBehavior(userId: Long, foodId: Long, type: String)

    // 🔢 Kiểm tra tồn tại hành vi
    @Query("""
        SELECT COUNT(*) 
        FROM user_behavior 
        WHERE userId = :userId AND foodId = :foodId 
        AND behaviorType = :type
    """)
    suspend fun exists(userId: Long, foodId: Long, type: String): Int

    // 📋 Lấy tất cả hành vi chia sẻ
    @Query("SELECT * FROM user_behavior WHERE behaviorType = 'shared'")
    suspend fun getAllSharedBehavior(): List<UserBehaviorEntity>
}
