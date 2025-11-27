package com.example.my_story_rc.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_progress")
data class StoryProgress(
    @PrimaryKey val storyId: Int, // ID истории из вашего списка
    val isCompleted: Boolean,
    val lastReadChapter: Int, // Если нужен прогресс по главам
    val readTimeMs: Long // Если хотите отслеживать время чтения
)