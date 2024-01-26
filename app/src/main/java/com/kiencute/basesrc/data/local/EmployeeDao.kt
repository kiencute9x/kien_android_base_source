package com.kiencute.basesrc.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kiencute.basesrc.data.entities.Employee

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employee")
    fun getAllEmployee() : LiveData<List<Employee>>

    @Query("SELECT * FROM employee WHERE id = :id")
    fun getEmployee(id: Int): LiveData<Employee>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<Employee>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: Employee)

}