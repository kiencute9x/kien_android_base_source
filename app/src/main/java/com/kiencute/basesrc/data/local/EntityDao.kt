package com.kiencute.basesrc.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kiencute.basesrc.data.entities.Entity

@Dao
interface EntityDao {
    @Query("SELECT * FROM entity")
    fun getAllEntities() : LiveData<List<Entity>>

    @Query("SELECT * FROM entity WHERE id = :id")
    fun getEntity(id: Int): LiveData<Entity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(beers: List<Entity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(beer: Entity)

}