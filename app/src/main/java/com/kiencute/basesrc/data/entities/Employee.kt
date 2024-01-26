package com.kiencute.basesrc.data.entities

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "employee")
data class Employee(
    val id: Int,
    val imageUrl: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val contactNumber: String,
    val age: Int,
    val dob: String,
    val salary: Double,
    val address: String
) : Parcelable
