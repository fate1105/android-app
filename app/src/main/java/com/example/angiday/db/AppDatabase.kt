package com.example.angiday.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.angiday.db.dao.UserBehaviorDao
import com.example.angiday.db.dao.UserDao
import com.example.angiday.db.dao.UserProfileDao
import com.example.angiday.model.entity.*

@Database(
    entities = [
        FoodEntity::class,
        IngredientEntity::class,
        FoodIngredientCrossRef::class,
        CategoryEntity::class,
        TagEntity::class,
        FoodTagCrossRef::class,
        UserEntity::class,
        UserBehaviorEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun metaDao(): MetaDao
    abstract fun userDao(): UserDao
    abstract fun userBehaviorDao(): UserBehaviorDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "angiday.db"
                )
                    // 👉 Load từ assets 1 lần duy nhất
                    .createFromAsset("databases/angiday.db")

                    // ❌ KHÔNG XOÁ DB NỮA
                    // .fallbackToDestructiveMigration()  --> BỎ

                    // ⚠️ Nên tắt khi chạy production
                    .allowMainThreadQueries()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
