package com.example.my_story_rc.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: Int = 1, // Используем 1 как фиксированный ID для единственного пользователя
    val nickname: String,
    val dateOfBirth: Long, // Храним как timestamp (миллисекунды с эпохи)
    val reason: String,
    val avatarResId: Int,
    val isDarkModeEnabled: Boolean,
    val isNotificationsEnabled: Boolean
)