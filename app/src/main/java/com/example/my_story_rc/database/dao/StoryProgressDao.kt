package com.example.my_story_rc.database.dao

import androidx.room.*
import com.example.my_story_rc.database.entity.StoryProgress

@Dao
interface StoryProgressDao {
    @Query("SELECT * FROM story_progress WHERE storyId = :storyId LIMIT 1")
    suspend fun getProgress(storyId: Int): StoryProgress?

    @Query("SELECT * FROM story_progress")
    suspend fun getAllProgress(): List<StoryProgress>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(storyProgress: StoryProgress)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProgress(storyProgressList: List<StoryProgress>)

    @Update
    suspend fun updateProgress(storyProgress: StoryProgress)

    @Delete
    suspend fun deleteProgress(storyProgress: StoryProgress)

    @Query("DELETE FROM story_progress WHERE storyId = :storyId")
    suspend fun deleteProgressById(storyId: Int)
}