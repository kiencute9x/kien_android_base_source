package com.kiencute.landmarkremark.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.data.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // User Queries

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)




    // Note Queries
    @Query("SELECT * FROM notes WHERE userId = :userId")
    fun getNotesByUserId(userId: Int): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)
    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<Note>>

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNote(notes: List<Note>)

    @Query("SELECT * FROM notes WHERE noteId = :noteId")
    fun getNoteById(noteId: Int): Flow<Note>

}