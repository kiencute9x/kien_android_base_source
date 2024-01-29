package com.kiencute.basesrc.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kiencute.basesrc.data.entities.Beer

@Dao
interface BeerDao {
    @Query("SELECT * FROM beer")
    fun getAllBeers() : LiveData<List<Beer>>

    @Query("SELECT * FROM beer WHERE id = :id")
    fun getBeer(id: Int): LiveData<Beer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(beers: List<Beer>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(beer: Beer)

}