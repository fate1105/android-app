package com.example.angiday.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.angiday.model.entity.*

@Database(
    entities = [
        FoodEntity::class,
        IngredientEntity::class,
        FoodIngredientCrossRef::class,
        CategoryEntity::class,
        TagEntity::class,
        FoodTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun metaDao(): MetaDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "angiday.db"
                )
                    // Copy DB có sẵn từ assets/databases/angiday.db
                    .createFromAsset("databases/angiday.db")
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }

}
