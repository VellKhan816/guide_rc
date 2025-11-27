package com.example.my_story_rc.database

import com.example.my_story_rc.database.dao.StoryProgressDao
import com.example.my_story_rc.database.dao.UserDao
import com.example.my_story_rc.database.entity.StoryProgress
import com.example.my_story_rc.database.entity.User

class RoomRepository(
    private val userDao: UserDao,
    private val storyProgressDao: StoryProgressDao // Добавьте, если используете
) {
    // User
    suspend fun getUser(uid: Int = 1) = userDao.getUser(uid)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)

    // Story Progress (если используется)
    suspend fun getStoryProgress(storyId: Int) = storyProgressDao.getProgress(storyId)
    suspend fun getAllStoryProgress() = storyProgressDao.getAllProgress()
    suspend fun insertStoryProgress(progress: StoryProgress) = storyProgressDao.insertProgress(progress)
    suspend fun insertAllStoryProgress(progressList: List<StoryProgress>) = storyProgressDao.insertAllProgress(progressList)
    suspend fun updateStoryProgress(progress: StoryProgress) = storyProgressDao.updateProgress(progress)
    suspend fun deleteStoryProgressById(storyId: Int) = storyProgressDao.deleteProgressById(storyId)
}