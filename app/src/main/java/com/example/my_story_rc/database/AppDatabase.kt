package com.example.my_story_rc.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.my_story_rc.database.dao.StoryProgressDao
import com.example.my_story_rc.database.dao.UserDao
import com.example.my_story_rc.database.entity.StoryProgress
import com.example.my_story_rc.database.entity.User

@Database(
    entities = [User::class, StoryProgress::class], // Добавьте StoryProgress, если используете
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun storyProgressDao(): StoryProgressDao // Добавьте, если используете

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Используйте миграции в продакшене
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}