package com.kiencute.landmarkremark.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.kiencute.landmarkremark.data.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
    @Transaction
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserWithNotes(userId: Int): Flow<UserWithNotes>

}