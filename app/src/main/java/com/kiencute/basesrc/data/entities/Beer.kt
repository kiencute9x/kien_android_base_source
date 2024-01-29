package com.kiencute.basesrc.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import javax.annotation.Nullable

@Parcelize
@Entity(tableName = "beer")
data class Beer(
    @PrimaryKey
    val id: Int,
    val name: String,
    val tagline: String,
    @SerializedName("image_url")
    val imageUrl: String,
) : Parcelable
