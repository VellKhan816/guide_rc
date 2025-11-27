package com.example.my_story_rc

import android.app.Application
import com.example.my_story_rc.database.AppDatabase
import com.example.my_story_rc.database.RoomRepository

class MyStoryApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val roomRepository by lazy { RoomRepository(database.userDao(), database.storyProgressDao()) }
}