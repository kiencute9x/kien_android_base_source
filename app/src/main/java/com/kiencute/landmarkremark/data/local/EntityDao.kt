package com.kiencute.landmarkremark.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kiencute.landmarkremark.data.entities.Entity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntityDao {
    @Query("SELECT * FROM entity")
    fun getAllEntities(): Flow<List<Entity>>

    @Query("SELECT * FROM entity WHERE id = :id")
    fun getEntity(id: Int): Flow<Entity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<Entity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: Entity)
}
