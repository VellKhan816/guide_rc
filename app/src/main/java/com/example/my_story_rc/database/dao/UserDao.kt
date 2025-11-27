package com.example.my_story_rc.database.dao

import androidx.room.*
import com.example.my_story_rc.database.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUser(uid: Int = 1): User?

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun getUserAsFlow(uid: Int = 1): Flow<User?> // Для LiveData/Flow

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}