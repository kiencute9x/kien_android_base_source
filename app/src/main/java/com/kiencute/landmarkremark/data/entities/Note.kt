package com.kiencute.landmarkremark.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("userId"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Int,
    val userId: Int,
    val latitude: Double,
    val longitude: Double,
    val note: String
) : Parcelable {
    fun toJsonObject(): JsonObject {
        val gson = Gson()
        val jsonStr = gson.toJson(this)
        return gson.fromJson(jsonStr, JsonObject::class.java)
    }
}