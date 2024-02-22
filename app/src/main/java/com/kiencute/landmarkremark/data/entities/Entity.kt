package com.kiencute.landmarkremark.data.entities

import android.os.Parcelable
import androidx.core.app.GrammaticalInflectionManagerCompat.GrammaticalGender
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "entity")
data class Entity(
    @PrimaryKey
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val gender: String,
    val email: String,
    @SerializedName("ip_address")
    val ipAddress: String,
) : Parcelable
